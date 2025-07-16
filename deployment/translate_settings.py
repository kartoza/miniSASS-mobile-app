#!/usr/bin/env python3
"""
Script to translate Android strings.xml file using Google Translate (free)
"""

import xml.etree.ElementTree as ET
from googletrans import Translator
import time
import argparse
import os
from typing import Dict, List, Set
from concurrent.futures import ThreadPoolExecutor, as_completed
import threading

class AndroidStringsTranslator:
    def __init__(self, target_language: str = 'es', delay: float = 0.1, max_threads: int = 5):
        """
        Initialize the translator

        Args:
            target_language: Target language code (e.g., 'es' for Spanish, 'fr' for French)
            delay: Delay between API calls to avoid rate limiting
            max_threads: Maximum number of threads for concurrent translation
        """
        self.translator = Translator()
        self.target_language = target_language
        self.delay = delay
        self.max_threads = max_threads
        self.existing_translations: Dict[str, str] = {}
        self.existing_arrays: Dict[str, List[str]] = {}
        self.lock = threading.Lock()

    def read_existing_translations(self, output_file: str):
        """
        Read existing translations from output file to avoid re-translating

        Args:
            output_file: Path to output strings.xml file
        """
        if not os.path.exists(output_file):
            print(f"Output file {output_file} doesn't exist, will create new one")
            return

        try:
            tree = ET.parse(output_file)
            root = tree.getroot()

            # Read existing string translations
            for string_elem in root.findall('string'):
                name = string_elem.get('name')
                text = string_elem.text
                if name and text:
                    self.existing_translations[name] = text

            # Read existing string-array translations
            for array_elem in root.findall('string-array'):
                array_name = array_elem.get('name')
                if array_name:
                    items = []
                    for item_elem in array_elem.findall('item'):
                        if item_elem.text:
                            items.append(item_elem.text)
                    self.existing_arrays[array_name] = items

            print(f"Loaded {len(self.existing_translations)} existing string translations")
            print(f"Loaded {len(self.existing_arrays)} existing array translations")

        except Exception as e:
            print(f"Error reading existing translations: {e}")

    def translate_text(self, text: str) -> str:
        """
        Translate a single text string

        Args:
            text: Text to translate

        Returns:
            Translated text
        """
        try:
            # Add delay to avoid rate limiting
            time.sleep(self.delay)

            # Skip translation for very short strings or those that look like codes
            if len(text.strip()) <= 2 or text.isupper():
                return text

            with self.lock:
                result = self.translator.translate(text, dest=self.target_language)
                return result.text
        except Exception as e:
            print(f"Error translating '{text}': {e}")
            return text  # Return original text if translation fails

    def should_translate_string(self, name: str, text: str) -> bool:
        """
        Determine if a string should be translated based on its name and content

        Args:
            name: String resource name
            text: String content

        Returns:
            True if should translate, False otherwise
        """
        # Skip if already translated
        if name in self.existing_translations:
            return False

        # Skip certain types of strings
        skip_patterns = [
            'app_name',  # App name usually stays the same
            'url',       # URLs
            'api',       # API related
            'id',        # IDs
        ]

        # Skip if name contains skip patterns
        for pattern in skip_patterns:
            if pattern in name.lower().split('_'):
                return False

        # Skip very short strings
        if len(text.strip()) <= 2:
            return False

        # Skip strings that are all uppercase (likely constants)
        if text.isupper():
            return False

        return True

    def should_translate_array(self, array_name: str) -> bool:
        """
        Determine if a string array should be translated

        Args:
            array_name: Array name

        Returns:
            True if should translate, False otherwise
        """
        # Skip if already translated
        if array_name in self.existing_arrays:
            return False

        # Skip certain arrays that shouldn't be translated
        skip_arrays = ['countries_display_names', 'upload_preference_values']
        return array_name not in skip_arrays

    def translate_string_batch(self, string_items: List[tuple]) -> List[tuple]:
        """
        Translate a batch of strings using ThreadPoolExecutor

        Args:
            string_items: List of (name, original_text, string_elem) tuples

        Returns:
            List of (name, translated_text, string_elem) tuples
        """
        def translate_item(item):
            name, original_text, string_elem = item
            translated_text = self.translate_text(original_text)
            return (name, translated_text, string_elem)

        results = []
        with ThreadPoolExecutor(max_workers=self.max_threads) as executor:
            future_to_item = {executor.submit(translate_item, item): item for item in string_items}

            for future in as_completed(future_to_item):
                item = future_to_item[future]
                try:
                    result = future.result()
                    results.append(result)
                    print(f"Translated: {result[0]} = '{item[1][:50]}...' -> '{result[1][:50]}...'\n")
                except Exception as e:
                    name, original_text, string_elem = item
                    print(f"Error translating {name}: {e}\n")
                    results.append((name, original_text, string_elem))

        return results

    def translate_array_items_batch(self, array_items: List[tuple]) -> List[tuple]:
        """
        Translate array items using ThreadPoolExecutor

        Args:
            array_items: List of (array_name, item_index, original_text, item_elem) tuples

        Returns:
            List of (array_name, item_index, translated_text, item_elem) tuples
        """
        def translate_array_item(item):
            array_name, item_index, original_text, item_elem = item
            translated_text = self.translate_text(original_text)
            return (array_name, item_index, translated_text, item_elem)

        results = []
        with ThreadPoolExecutor(max_workers=self.max_threads) as executor:
            future_to_item = {executor.submit(translate_array_item, item): item for item in array_items}

            for future in as_completed(future_to_item):
                item = future_to_item[future]
                try:
                    result = future.result()
                    results.append(result)
                    array_name, item_index, original_text, item_elem = item
                    print(f"  Translated array item {array_name}[{item_index}]: '{original_text[:30]}...' -> '{result[2][:30]}...'\n")
                except Exception as e:
                    array_name, item_index, original_text, item_elem = item
                    print(f"Error translating {array_name}[{item_index}]: {e}\n")
                    results.append((array_name, item_index, original_text, item_elem))

        return results

    def translate_strings_xml(self, input_file: str, output_file: str):
        """
        Translate an Android strings.xml file

        Args:
            input_file: Path to input strings.xml file
            output_file: Path to output translated strings.xml file
        """
        try:
            # Read existing translations first
            self.read_existing_translations(output_file)

            # Parse the input XML file
            tree = ET.parse(input_file)
            root = tree.getroot()

            # If output file exists, use it as base, otherwise use input
            if os.path.exists(output_file):
                output_tree = ET.parse(output_file)
                output_root = output_tree.getroot()
                print("Using existing output file as base")
            else:
                output_tree = tree
                output_root = root
                print("Creating new output file from input")

            translated_count = 0
            skipped_count = 0

            print(f"Starting translation to {self.target_language}...")

            # Collect strings that need translation
            strings_to_translate = []
            for string_elem in root.findall('string'):
                name = string_elem.get('name')
                original_text = string_elem.text

                if original_text and self.should_translate_string(name, original_text):
                    strings_to_translate.append((name, original_text, string_elem))
                else:
                    if name in self.existing_translations:
                        print(f"Skipping (already translated): {name}")
                    else:
                        print(f"Skipping (rule-based): {name}")
                    skipped_count += 1

            # Translate strings in batches using ThreadPoolExecutor
            if strings_to_translate:
                print(f"Translating {len(strings_to_translate)} strings...")
                translated_strings = self.translate_string_batch(strings_to_translate)

                # Update the output tree with translations
                for name, translated_text, _ in translated_strings:
                    # Find or create the string element in output tree
                    output_string_elem = output_root.find(f"string[@name='{name}']")
                    if output_string_elem is None:
                        output_string_elem = ET.SubElement(output_root, 'string', name=name)
                    output_string_elem.text = translated_text
                    translated_count += 1

            # Process string-array elements
            arrays_to_translate = []
            for array_elem in root.findall('string-array'):
                array_name = array_elem.get('name')
                print(f"Processing array: {array_name}")

                if not self.should_translate_array(array_name):
                    if array_name in self.existing_arrays:
                        print(f"Skipping array (already translated): {array_name}")
                    else:
                        print(f"Skipping array (should not be translated): {array_name}")
                    continue

                # Collect array items that need translation
                for item_index, item_elem in enumerate(array_elem.findall('item')):
                    original_text = item_elem.text
                    if original_text and len(original_text.strip()) > 2:
                        arrays_to_translate.append((array_name, item_index, original_text, item_elem))

            # Translate array items in batches
            if arrays_to_translate:
                print(f"Translating {len(arrays_to_translate)} array items...")
                translated_array_items = self.translate_array_items_batch(arrays_to_translate)

                # Group results by array name
                array_translations = {}
                for array_name, item_index, translated_text, _ in translated_array_items:
                    if array_name not in array_translations:
                        array_translations[array_name] = {}
                    array_translations[array_name][item_index] = translated_text

                # Update the output tree with array translations
                for array_name, item_translations in array_translations.items():
                    # Find or create the array element in output tree
                    output_array_elem = output_root.find(f"string-array[@name='{array_name}']")
                    if output_array_elem is None:
                        output_array_elem = ET.SubElement(output_root, 'string-array', name=array_name)

                    # Clear existing items and add translated ones
                    for item_elem in output_array_elem.findall('item'):
                        output_array_elem.remove(item_elem)

                    # Add translated items in order
                    for item_index in sorted(item_translations.keys()):
                        item_elem = ET.SubElement(output_array_elem, 'item')
                        item_elem.text = item_translations[item_index]
                        translated_count += 1

            # Write the translated XML to output file
            output_tree.write(output_file, encoding='utf-8', xml_declaration=True)

            print(f"\nTranslation complete!")
            print(f"Translated: {translated_count} items")
            print(f"Skipped: {skipped_count} items")
            print(f"Output saved to: {output_file}")

        except Exception as e:
            print(f"Error processing XML file: {e}")

    def create_values_folder_structure(self, base_path: str, language_code: str):
        """
        Create the proper Android values folder structure for the target language

        Args:
            base_path: Base path (e.g., 'app/src/main/res')
            language_code: Language code (e.g., 'es', 'fr')
        """
        values_folder = os.path.join(base_path, f'values-{language_code}')
        os.makedirs(values_folder, exist_ok=True)
        return os.path.join(values_folder, 'strings.xml')

def main():
    parser = argparse.ArgumentParser(description='Translate Android strings.xml file')
    parser.add_argument('input_file', help='Path to input strings.xml file')
    parser.add_argument('-l', '--language', default='es',
                       help='Target language code (default: es for Spanish)')
    parser.add_argument('-o', '--output', help='Output file path')
    parser.add_argument('-d', '--delay', type=float, default=0.1,
                       help='Delay between API calls in seconds (default: 0.1)')
    parser.add_argument('-t', '--threads', type=int, default=5,
                       help='Maximum number of threads for concurrent translation (default: 5)')
    parser.add_argument('--create-folder', action='store_true',
                       help='Create proper Android values-{lang} folder structure')

    args = parser.parse_args()

    # Determine output file path
    if args.output:
        output_file = args.output
    elif args.create_folder:
        # Extract base res path from input file
        input_dir = os.path.dirname(args.input_file)
        res_path = os.path.dirname(input_dir)  # Go up from values to res

        translator = AndroidStringsTranslator(args.language, args.delay, args.threads)
        output_file = translator.create_values_folder_structure(res_path, args.language)
    else:
        # Default output file
        base_name = os.path.splitext(args.input_file)[0]
        output_file = f"{base_name}_{args.language}.xml"

    # Create translator and translate
    translator = AndroidStringsTranslator(args.language, args.delay, args.threads)
    translator.translate_strings_xml(args.input_file, output_file)

if __name__ == "__main__":
    main()
