# SkDeobfuscator
Deobfuscator dla skryptów zaciemnionych WildScriptem

## Użycie:
Odpalić poleceniem java -jar <nazwa pliku> z konsoli, lub uzywając skytpu.<br>
Obok pliku .jar powinien znajdować się plik in.txt (chyba że podamy inaczej, patrz `Argumenty`) gdzie linijka po linijce podane są moce i pliki do rozkodowania, scieżki mogą być relatywane do programu.<br>
```
350 T:/Inne/plik.txt
? in/plik.txt
```
Znak zapytania symbolizuje że nie znasz mocy jakiej powinieneś użyć, więc program znajdzie ją sam. <br>

## Argumenty
Dodatkowo odpalając program można podać do 3 argumentów: <br>
`java -jar SkDeobfuscator.jar -nobad <in.txt path> <out folder path>` <br>
Lecz nie są one wymagane, każdy z nich ma domyślne wartości. <br><br>
**Argument** `-nobad` jest opcjonalny, powoduje on że plugin nie sprawdza poprawności znaków bo próbie odgadnięcia mocy, kodując plik z mocą np 500, da sie go odkodować używając mocy zblizonych, np 497, ale wtedy niektóre znaki są inne niż powinny, szczególnie polskie litery, domyślnie program stara się odszukać takie problemy i je naprawić, ta opcja pozwala to ominąć. (jeśli powoduje to jakieś problemy) <br><br>
**Drugi** (może być pierwszy jeśli nie podasz -nobad) argument to scieżka do pliku wyjściowego, domyślnie jest to `in.txt` <br><br>
**Trzeci** (lub drugi) argument to scieżka do folderu gdzie wylądują już rozkodowane skrypty, domyślnie jest to `out` <br>

## Inne
Dodatkowo, jeśli po uzyciu podanej przez ciebie mocy, program wykryje że jednak ta moc nie jest poprawna, to automatycznie znacznie szukanie poprawnej.<br>
Szukanie jest bardzo szybkie, około 1000 sprawdzeń na sekundę, więc nawet rozkodowanie dużej ilości plików nie powinno trwać więcej niz minute ;)
