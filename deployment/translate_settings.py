#!/usr/bin/env python3
"""
Script to translate Android strings.xml file using Google Translate (free)
"""

import xml.etree.ElementTree as ET
from googletrans import Translator
import time
import argparse
import os
from typing import Dict, List

class AndroidStringsTranslator:
    def __init__(self, target_language: str = 'es', delay: float = 0.1):
        """
        Initialize the translator

        Args:
            target_language: Target language code (e.g., 'es' for Spanish, 'fr' for French)
            delay: Delay between API calls to avoid rate limiting
        """
        self.translator = Translator()
        self.target_language = target_language
        self.delay = delay

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
        # Skip certain types of strings
        skip_patterns = [
            'app_name',  # App name usually stays the same
            'url',       # URLs
            'api',       # API related
            'key',       # Keys
            'id',        # IDs
        ]

        # Skip if name contains skip patterns
        for pattern in skip_patterns:
            if pattern in name.lower():
                return False

        # Skip very short strings
        if len(text.strip()) <= 2:
            return False

        # Skip strings that are all uppercase (likely constants)
        if text.isupper():
            return False

        return True

    def translate_strings_xml(self, input_file: str, output_file: str):
        """
        Translate an Android strings.xml file

        Args:
            input_file: Path to input strings.xml file
            output_file: Path to output translated strings.xml file
        """
        try:
            # Parse the XML file
            tree = ET.parse(input_file)
            root = tree.getroot()

            translated_count = 0
            skipped_count = 0

            print(f"Starting translation to {self.target_language}...")

            # Process all string elements
            for string_elem in root.findall('string'):
                name = string_elem.get('name')
                original_text = string_elem.text

                if original_text and self.should_translate_string(name, original_text):
                    print(f"Translating: {name} = '{original_text[:50]}...'")
                    translated_text = self.translate_text(original_text)
                    string_elem.text = translated_text
                    translated_count += 1
                else:
                    print(f"Skipping: {name}")
                    skipped_count += 1

            # Process string-array elements
            for array_elem in root.findall('string-array'):
                array_name = array_elem.get('name')
                print(f"Processing array: {array_name}")

                # Skip certain arrays that shouldn't be translated
                if array_name in ['countries_display_names', 'upload_preference_values']:
                    print(f"Skipping array: {array_name} (should not be translated)")
                    continue

                for item_elem in array_elem.findall('item'):
                    original_text = item_elem.text
                    if original_text and len(original_text.strip()) > 2:
                        print(f"  Translating item: '{original_text[:30]}...'")
                        translated_text = self.translate_text(original_text)
                        item_elem.text = translated_text
                        translated_count += 1
                    else:
                        skipped_count += 1

            # Write the translated XML to output file
            tree.write(output_file, encoding='utf-8', xml_declaration=True)

            print(f"\nTranslation complete!")
            print(f"Translated: {translated_count} strings")
            print(f"Skipped: {skipped_count} strings")
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

        translator = AndroidStringsTranslator(args.language, args.delay)
        output_file = translator.create_values_folder_structure(res_path, args.language)
    else:
        # Default output file
        base_name = os.path.splitext(args.input_file)[0]
        output_file = f"{base_name}_{args.language}.xml"

    # Create translator and translate
    translator = AndroidStringsTranslator(args.language, args.delay)
    translator.translate_strings_xml(args.input_file, output_file)

if __name__ == "__main__":
    main()
