package com.rk.amii.utils;

public class Constants {
    // Country data
    public static final String[] COUNTRIES = {
            "Aruba", "Afghanistan", "Angola", "Anguilla", "Åland Islands", "Albania", "Andorra", "United Arab Emirates",
            "Argentina", "Armenia", "American Samoa", "Antarctica", "French Southern Territories", "Antigua and Barbuda",
            "Australia", "Austria", "Azerbaijan", "Burundi", "Belgium", "Benin", "Bonaire, Sint Eustatius and Saba",
            "Burkina Faso", "Bangladesh", "Bulgaria", "Bahrain", "Bahamas", "Bosnia and Herzegovina", "Saint Barthélemy",
            "Belarus", "Belize", "Bermuda", "Bolivia, Plurinational State of", "Brazil", "Barbados", "Brunei Darussalam",
            "Bhutan", "Bouvet Island", "Botswana", "Central African Republic", "Canada", "Cocos (Keeling) Islands",
            "Switzerland", "Chile", "China", "Côte d'Ivoire", "Cameroon", "Congo, The Democratic Republic of the", "Congo",
            "Cook Islands", "Colombia", "Comoros", "Cabo Verde", "Costa Rica", "Cuba", "Curaçao", "Christmas Island",
            "Cayman Islands", "Cyprus", "Czechia", "Germany", "Djibouti", "Dominica", "Denmark", "Dominican Republic",
            "Algeria", "Ecuador", "Egypt", "Eritrea", "Western Sahara", "Spain", "Estonia", "Ethiopia", "Finland", "Fiji",
            "Falkland Islands (Malvinas)", "France", "Faroe Islands", "Micronesia, Federated States of", "Gabon",
            "United Kingdom", "Georgia", "Guernsey", "Ghana", "Gibraltar", "Guinea", "Guadeloupe", "Gambia", "Guinea-Bissau",
            "Equatorial Guinea", "Greece", "Grenada", "Greenland", "Guatemala", "French Guiana", "Guam", "Guyana",
            "Hong Kong", "Heard Island and McDonald Islands", "Honduras", "Croatia", "Haiti", "Hungary", "Indonesia",
            "Isle of Man", "India", "British Indian Ocean Territory", "Ireland", "Iran, Islamic Republic of", "Iraq",
            "Iceland", "Israel", "Italy", "Jamaica", "Jersey", "Jordan", "Japan", "Kazakhstan", "Kenya", "Kyrgyzstan",
            "Cambodia", "Kiribati", "Saint Kitts and Nevis", "Korea, Republic of", "Kuwait", "Lao People's Democratic Republic",
            "Lebanon", "Liberia", "Libya", "Saint Lucia", "Liechtenstein", "Sri Lanka", "Lesotho", "Lithuania", "Luxembourg",
            "Latvia", "Macao", "Saint Martin (French part)", "Morocco", "Monaco", "Moldova, Republic of", "Madagascar",
            "Maldives", "Mexico", "Marshall Islands", "North Macedonia", "Mali", "Malta", "Myanmar", "Montenegro", "Mongolia",
            "Northern Mariana Islands", "Mozambique", "Mauritania", "Montserrat", "Martinique", "Mauritius", "Malawi",
            "Malaysia", "Mayotte", "Namibia", "New Caledonia", "Niger", "Norfolk Island", "Nigeria", "Nicaragua", "Niue",
            "Netherlands", "Norway", "Nepal", "Nauru", "New Zealand", "Oman", "Pakistan", "Panama", "Pitcairn", "Peru",
            "Philippines", "Palau", "Papua New Guinea", "Poland", "Puerto Rico", "Korea, Democratic People's Republic of",
            "Portugal", "Paraguay", "Palestine, State of", "French Polynesia", "Qatar", "Réunion", "Romania", "Russian Federation",
            "Rwanda", "Saudi Arabia", "Sudan", "Senegal", "Singapore", "South Georgia and the South Sandwich Islands",
            "Saint Helena, Ascension and Tristan da Cunha", "Svalbard and Jan Mayen", "Solomon Islands", "Sierra Leone",
            "El Salvador", "San Marino", "Somalia", "Saint Pierre and Miquelon", "Serbia", "South Sudan", "Sao Tome and Principe",
            "Suriname", "Slovakia", "Slovenia", "Sweden", "Eswatini", "Sint Maarten (Dutch part)", "Seychelles",
            "Syrian Arab Republic", "Turks and Caicos Islands", "Chad", "Togo", "Thailand", "Tajikistan", "Tokelau",
            "Turkmenistan", "Timor-Leste", "Tonga", "Trinidad and Tobago", "Tunisia", "Türkiye", "Tuvalu",
            "Taiwan, Province of China", "Tanzania, United Republic of", "Uganda", "Ukraine", "United States Minor Outlying Islands",
            "Uruguay", "United States", "Uzbekistan", "Holy See (Vatican City State)", "Saint Vincent and the Grenadines",
            "Venezuela, Bolivarian Republic of", "Virgin Islands, British", "Virgin Islands, U.S.", "Viet Nam", "Vanuatu",
            "Wallis and Futuna", "Samoa", "Yemen", "South Africa", "Zambia", "Zimbabwe"
    };
    public static final String[] COUNTRY_CODES = {"AW", "AF", "AO", "AI", "AX", "AL", "AD", "AE", "AR", "AM", "AS", "AQ", "TF", "AG", "AU", "AT", "AZ", "BI", "BE", "BJ", "BQ", "BF", "BD", "BG", "BH", "BS", "BA", "BL", "BY", "BZ", "BM", "BO", "BR", "BB", "BN", "BT", "BV", "BW", "CF", "CA", "CC", "CH", "CL", "CN", "CI", "CM", "CD", "CG", "CK", "CO", "KM", "CV", "CR", "CU", "CW", "CX", "KY", "CY", "CZ", "DE", "DJ", "DM", "DK", "DO", "DZ", "EC", "EG", "ER", "EH", "ES", "EE", "ET", "FI", "FJ", "FK", "FR", "FO", "FM", "GA", "GB", "GE", "GG", "GH", "GI", "GN", "GP", "GM", "GW", "GQ", "GR", "GD", "GL", "GT", "GF", "GU", "GY", "HK", "HM", "HN", "HR", "HT", "HU", "ID", "IM", "IN", "IO", "IE", "IR", "IQ", "IS", "IL", "IT", "JM", "JE", "JO", "JP", "KZ", "KE", "KG", "KH", "KI", "KN", "KR", "KW", "LA", "LB", "LR", "LY", "LC", "LI", "LK", "LS", "LT", "LU", "LV", "MO", "MF", "MA", "MC", "MD", "MG", "MV", "MX", "MH", "MK", "ML", "MT", "MM", "ME", "MN", "MP", "MZ", "MR", "MS", "MQ", "MU", "MW", "MY", "YT", "NA", "NC", "NE", "NF", "NG", "NI", "NU", "NL", "NO", "NP", "NR", "NZ", "OM", "PK", "PA", "PN", "PE", "PH", "PW", "PG", "PL", "PR", "KP", "PT", "PY", "PS", "PF", "QA", "RE", "RO", "RU", "RW", "SA", "SD", "SN", "SG", "GS", "SH", "SJ", "SB", "SL", "SV", "SM", "SO", "PM", "RS", "SS", "ST", "SR", "SK", "SI", "SE", "SZ", "SX", "SC", "SY", "TC", "TD", "TG", "TH", "TJ", "TK", "TM", "TL", "TO", "TT", "TN", "TR", "TV", "TW", "TZ", "UG", "UA", "UM", "UY", "US", "UZ", "VA", "VC", "VE", "VG", "VI", "VN", "VU", "WF", "WS", "YE", "ZA", "ZM", "ZW"};
    // Private constructor to prevent instantiation
    private Constants() {}
}
