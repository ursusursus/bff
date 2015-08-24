<h1>Big files finder</h1>
<b>Zadaním úlohy bolo nájsť N najväčších súborov vo zvolených adresároch.</b>

Aplikácia sa skladá z 2 architekturálnych častí</br></br>

<h3>MainActivity</h3>
&nbsp;&nbsp;&nbsp;Predstavuje „klientskú“ časť, obsahuje GUI ktoré ovláda vstupy pre servisu. Skladá sa zo 4 fragmentov

<p><b>WelcomeFragment</b></br>
&nbsp;&nbsp;&nbsp;Predstavuje len úvodnú obrazovku, ukážka komplexnejších animačných techník a material designu</p>

<p><b>FolderPickerFragment</b></br>
&nbsp;&nbsp;&nbsp;UI pre výber adresárov, je možné prechádzať aktuálnu súborovú štruktúru a prídávať si adresáre, ktoré budú prehľadávané. Zvolené adresáre su zobrazené v FileChipsView, čo je subclassa FlowLayout-u, ktorá je obdobou LinearLayoutu taká, že ak sa View zmestí do riadku tak ho dá do toho istého riadku, ak nie tak na nový riadok. 
Ako list je použitý RecyclerView, ktorý sa lepšie chová s Transition animáciami ako ListView. V pravej časti riadku je taktiež znázornený stav súboru (zašktrnutie / plus)</p>

<p><b>CountPickerFragment</b></br>
&nbsp;&nbsp;&nbsp;Zvolenie počtu najväčších hľadaných súborov</p>

<p><b>ResultsFragment</b></br>
&nbsp;&nbsp;&nbsp;Reaguje na broadcasty FinderServicu, a zobrazuje progressbar/výsledky/chybu výpočtu.
Kde každý z nich predstavuje logický krok zadávania vstupov pre výpočet.</p>

<h3>FinderService</h3>
<p>&nbsp;&nbsp;&nbsp;Predstavuje „serverovú“ časť, ktorá vykonáva samotné prehľadávanie. Service bol zvolený preto lebo aktivita je „len GUI“, ktoré po odchode do pozadia,môže kedykoľvek zaniknúť a preto nie je vhodným rodičom pre potenciálne dlhotrvajúce thready výpočtu. Tzn, výpočeť je nezávislý od GUI, a to je dobre. Komunikácia medzi nimi prebieha pomocou intentov (broadcastov)</p>

<p>&nbsp;&nbsp;&nbsp;Vstupom pre service je počet koľko najvačších súborov hľadáme a cesty na adresáre, ktoré majú byť prehľadávané.</p>

<p>&nbsp;&nbsp;&nbsp;Po očístení o neplatné vstupy, service optimalizuje zadané cesty adresárov vyhodením duplikátov a ciest ktoré su podadresárom niektorých z ostatných ciest a nemá zmysel ich prehľadávať (v princípe, algoritmus kontroluje prefixy ciest)</p>
<p>&nbsp;&nbsp;&nbsp;Potom pre každý prekonvertovaný File, vytvára FindLargestFilesTask asynchrónny task, ktorý beží na vlastnom Thread-e. Tasky sú spúšťané na exekútore, tzn. všetky thready bežia paralelne per-file. V tele tasku sa prehľadáva cesta rekurízvne a ak súbor nie je adresár je pridaný do kolekcie.</p>

<p>&nbsp;&nbsp;&nbsp;Zvolená kolekcia FilesBoundedPriorityQueue, ktorá predstavuje subclass-u PriorityQueue, čo je štandardná implementácia heap-u, ktorá nám zaručí polo-zoradenie súborov s garantovaným najmenším súborom na vrchole. Pri pridávaní je avšak zvolená optimalizácia, kedže vieme koľko max. súborov hľadáme, teda nemá zmysel, aby v heap-e boli všetky súbory ale len najvačších N v danom momente. Teda pri pridávaní sa kontroluje, čí je vstup väčší ako súbor na vrchole (najmenší z heap-u), taký súbor je do heap-u pridaný a vrchol odstránený. Takto časom dostaneme N najvačších súborov. (ešte podotknúť, že heap-a je zoradená vzostupne, tzn najmenší je navrchole, teda je po vybratí prvkov z nej do listu, je potrebné list otočiť). Taktiež metóda add() je synchronizovaná a garantuje thread-safety, keďže heap je zdieľaný medzi thread-mi.</p>

<p>&nbsp;&nbsp;&nbsp;Po dokončení úlohy, je referencia naň odobratá zo Set-u bežiacich úloh. Ak je set prázdny, boli vykonané všetky úlohy, teda výsledky vraciame cez broadcast naspať do UI a service ukončujeme. (Samozrejme o začatí a skončení výpočtov notifikuje systémová notifikácia s progress-barom, resp. jednorázová o ukončení všetkých výpočtov.). Pre prenos výsledkov bol použítý ParcelableFile, čo je len POJO cesty, názvu a veľkosti súboru, keďže systémový File neimplementuje Parcelable interface a nie je možné preposlať ArrayList File-ov cez intent.</p>


