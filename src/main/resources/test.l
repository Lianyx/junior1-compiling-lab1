digit 0|1|2|3|4|5|6|7|8|9
letter a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z


%%

" |\t|\n" {}
"class|public|protected|private|void|static|int|char|float|double|String|if|else|switch|case|for|do|while|try|catch" { printf("saved word: %s\n", lexeme); }
"{letter}({letter}|{digit})*" { printf("identifier: %s\n", lexeme); }
"{digit}{digit}*" { printf("number: %s\n", lexeme); }
"+|+=|-|-=|\*|\*=|/|/=|=|==|&|&&|\||\|\||!|!=|<|<=|>|>=" { printf("operater: %s\n", lexeme); }
";|\{|\}|\(|\)|\[|\]|," { printf("delimiter: %s\n", lexeme); }
