# Rapport pour Tâche 2

Génération de tests avec ChatUniTest et analyse par mutation

## 1. Classes sélectionnées

Nous avons choisi deux classes du module `tika-core` :

- `org.apache.tika.utils.CharsetUtils`
- `org.apache.tika.utils.ServiceLoaderUtils`

Ces deux classes ont été sélectionnées parce qu'elles sont relativement petites, contiennent plusieurs branches de 
contrôle intéressantes à tester et possèdent déjà des tests unitaires existants, ce qui permet de comparer les tests 
générés automatiquement avec les tests écrits manuellement.

Avant l'ajout de nouveaux tests, l'analyse avec PIT sur ces deux classes donnait les résultats suivants :

- Couverture de lignes : **68/92 = 74 %**
- Mutants générés : **28**
- Mutants tués : **22**
- Couverture de mutation : **79 %**
- Mutants sans couverture : **6**
- Test strength : **100 %**

Plus précisément :

- `CharsetUtils` : **20/25 mutants tués = 80 %**
- `ServiceLoaderUtils` : **2/3 mutants tués = 67 %**

## 2. Génération des tests avec ChatUniTest

Les tests ont été générés avec ChatUniTest en utilisant le modèle local `codeqwen:v1.5-chat` via Ollama.

Les fichiers produits par ChatUniTest ont été placés dans le répertoire :

` tika-core/chatunitest-tests/ `

### 2.1. CharsetUtils

Pour `CharsetUtils`, ChatUniTest a généré des tests pour trois méthodes :

- `clean`
- `forName`
- `isSupported`

Au total, les fichiers générés contenaient **22 méthodes de test** :

- 18 tests pour `clean`
- 1 test pour `forName`
- 3 tests pour `isSupported`

Lors de la validation des tests générés pour `clean`, ChatUniTest a exécuté 18 tests :

- 13 tests réussis
- 5 tests échoués

La génération elle-même s'est terminée avec `BUILD SUCCESS`, mais ce résultat signifie seulement que le processus 
ChatUniTest s'est terminé correctement. Il ne signifie pas que tous les tests générés étaient valides.

Les principaux fichiers générés étaient :

- `CharsetUtils_clean_2_1_Test.java`
- `CharsetUtils_forName_3_0_Test.java`
- `CharsetUtils_isSupported_1_0_Test.java`
- `CharsetUtils_Suite.java`

### 2.2. ServiceLoaderUtils

Pour `ServiceLoaderUtils`, ChatUniTest a réussi à produire un test compilable pour la méthode `sortLoadedClasses`.

Le fichier conservé était :

- `ServiceLoaderUtils_sortLoadedClasses_0_1_Test.java`

La génération des tests pour `newInstance` a été plus difficile. ChatUniTest a tenté de produire trois tests différents 
pour cette méthode.

Pour chacun de ces tests :

- la compilation a échoué au round 0 ;
- une première correction automatique a été tentée au round 1 ;
- une deuxième correction automatique a été tentée au round 2 ;
- la compilation a encore échoué après le round 2.

Aucun test compilable pour `newInstance` n'a donc été conservé par ChatUniTest.

Le processus global s'est néanmoins terminé avec `BUILD SUCCESS`, car le plugin avait terminé son exécution même si 
certains tests n'avaient pas pu être générés avec succès.

## 3. Analyse qualitative des tests générés

### 3.1. Tests générés pour CharsetUtils

Les tests générés par ChatUniTest présentent à la fois des points positifs et plusieurs limites.

Pour certaines situations simples, les oracles générés étaient corrects et précis. Par exemple, ChatUniTest vérifie 
correctement qu'un charset valide comme `UTF-8` est reconnu et qu'un charset invalide retourne `false`. Il a également 
généré un test utilisant `assertThrows` pour vérifier que `CharsetUtils.forName(null)` lève une 
`IllegalArgumentException`.

Cependant, plusieurs problèmes ont été observés.

#### Oracles incorrects

Parmi les 18 tests générés pour `clean`, 5 tests ont échoué lors de la validation. Dans ces cas, ChatUniTest avait 
généré une valeur attendue incorrecte.

Par exemple, certains tests supposaient que :

- `"[UTF-8]"` devait être transformé en `"UTF-8"`;
- `"UTF-8]"` devait être transformé en `"UTF-8"`;
- `"ISO 8859-1"` devait être transformé en `"ISO-8859-1"`;
- `"CP1252"` devait retourner exactement `"CP1252"`;
- `"Win1252"` devait retourner `"Windows-1252"`.

Ces résultats attendus ne correspondaient pas au comportement réel de `CharsetUtils`.

#### Test sans oracle

Un des tests générés pour `clean` appelait simplement la méthode avec la valeur `"NonStandardCharset"` sans effectuer 
aucune assertion.

Dans ce cas, le test exécute la méthode mais ne vérifie pas son résultat. Il ne possède donc pas réellement d'oracle 
et son pouvoir de détection est très faible.

#### Tests redondants

Certains tests générés vérifiaient essentiellement le même comportement. Par exemple, deux tests différents utilisaient 
la même entrée `"\"UTF-8\""` et attendaient tous deux `"UTF-8"`.

Cela augmente le nombre de tests sans nécessairement augmenter la couverture comportementale.

#### Utilisation inutile de la réflexion

ChatUniTest utilisait également la réflexion avec `getDeclaredMethod`, `setAccessible(true)` et `invoke` pour appeler 
des méthodes qui pouvaient être invoquées directement.

Cette approche rend les tests plus complexes et moins lisibles que les tests manuels déjà présents dans le projet.

Les tests existants de `CharsetUtilsTest` appellent directement les méthodes `CharsetUtils.isSupported(...)` 
et `CharsetUtils.clean(...)`, avec des assertions simples et lisibles. Ils couvrent notamment les charsets valides et 
invalides, les valeurs `null`, les chaînes vides, les guillemets et plusieurs formes particulières de noms de charset.

### 3.2. Tests générés pour ServiceLoaderUtils

Pour `ServiceLoaderUtils`, ChatUniTest a généré un test compilable pour `sortLoadedClasses`.

L'oracle du test était précis puisqu'il comparait la liste complète après l'appel de la méthode. Toutefois, le test 
utilisait uniquement des objets `String`, ce qui ne correspond pas au comportement principal que la méthode doit 
vérifier.

Le test manuel existant utilise au contraire plusieurs implémentations réelles de `Detector`, dont une classe 
personnalisée, puis mélange la liste plusieurs fois avant de vérifier l'ordre final. Il teste donc plus directement 
le comportement réel de `sortLoadedClasses`.

Le test généré contenait également du code inutile :

- un objet `CompareUtils` était créé;
- Mockito était configuré;
- de la réflexion était utilisée;

mais cet objet n'était finalement pas utilisé dans le test.

La principale limite observée concerne la méthode `newInstance`. ChatUniTest a tenté de générer plusieurs tests pour 
cette méthode, mais aucun n'a pu être compilé après les tentatives automatiques de correction.

Cela signifie que ChatUniTest a réussi à produire un test pour la méthode déjà relativement bien couverte 
`sortLoadedClasses`, mais n'a pas réussi à produire un test exploitable pour `newInstance`, qui représentait justement 
la partie la plus intéressante pour améliorer la couverture de mutation.

### 3.3. Comparaison générale avec les tests manuels

De manière générale, les tests générés par ChatUniTest ont permis de produire rapidement plusieurs scénarios de test, 
notamment des cas limites.

Cependant, les tests manuels existants étaient généralement :

- plus courts;
- plus lisibles;
- plus directement liés au comportement attendu;
- moins redondants;
- et plus précis dans le choix des données de test.

Les tests générés par ChatUniTest ont également nécessité plusieurs corrections manuelles avant de pouvoir être 
intégrés proprement au projet.

## 4. Résultats après ajout des tests générés

Après correction des tests générés afin qu'ils puissent être compilés et exécutés correctement, les nouveaux tests ont 
été ajoutés au projet.

Le nombre total de tests est passé de **749 à 771** :

- 749 tests originaux
- 22 tests générés et corrigés
- 0 failure
- 0 error
- 3 tests skipped
- `BUILD SUCCESS`

Une nouvelle analyse de mutation avec PIT a ensuite été exécutée sur `CharsetUtils` et `ServiceLoaderUtils`.

Les résultats obtenus étaient les suivants :

| Mesure | Tests originaux | Tests originaux + tests générés |
|---|---|---|
| Line coverage | 68/92 = 74 % | 68/92 = 74 % |
| Mutants générés | 28 | 28 |
| Mutants tués | 22 | 22 |
| Mutation coverage | 79 % | 79 % |
| Mutants sans couverture | 6 | 6 |
| Test strength | 100 % | 100 % |

L'ajout des tests générés par ChatUniTest n'a donc pas amélioré le score de mutation.

Même si 22 nouveaux tests ont été ajoutés au projet, aucun mutant supplémentaire n'a été détecté. Les 6 mutants qui 
n'étaient pas couverts avant l'ajout des tests générés sont restés non couverts.

Ce résultat montre que l'augmentation du nombre de tests ne signifie pas nécessairement une amélioration de leur 
pouvoir de détection. Plusieurs tests générés vérifiaient des comportements déjà couverts par les tests existants, 
tandis que les chemins responsables des mutants non détectés n'ont pas été atteints.

Après cette analyse, des tests supplémentaires ont donc été écrits manuellement afin de cibler directement les mutants 
encore non détectés.

## 5. Tests ajoutés manuellement

Après l'ajout des tests générés par ChatUniTest, 6 mutants restaient non détectés. Des tests ont donc été écrits 
manuellement afin de cibler précisément les chemins de code correspondants.

### 5.1. Tests manuels pour CharsetUtils

3 tests ont été ajoutés pour couvrir les chemins liés à l'intégration optionnelle d'ICU.

#### testIsSupportedUsesIcuHook

**Intention :**  
Vérifier que `CharsetUtils.isSupported()` utilise correctement le mécanisme ICU lorsqu'un hook `isSupportedICU` 
est disponible.

**Données de test :**  
La chaîne `"x-test-only-charset"` a été utilisée. Ce nom n'est pas reconnu comme un charset Java standard, ce qui 
permet de s'assurer que le résultat `true` provient bien du hook ICU injecté et non du comportement normal de Java.

Un faux hook ICU a été créé afin de toujours retourner `true`.

**Oracle :**  
Le test utilise :

`assertTrue(CharsetUtils.isSupported("x-test-only-charset"))`

Si le hook ICU est correctement utilisé, le résultat doit être `true`.

Ce test permet de détecter les mutants associés au chemin où `isSupportedICU` est disponible et où son résultat 
est utilisé.

#### testIsSupportedHandlesIcuInvocationFailure

**Intention :**  
Vérifier le comportement de `CharsetUtils.isSupported()` lorsqu'une erreur survient pendant l'appel du hook ICU.

**Données de test :**  
Le même nom `"x-test-only-charset"` est utilisé afin d'éviter qu'un charset standard soit reconnu avant d'atteindre 
le chemin ICU.

Cette fois, le faux hook ICU lève volontairement une `IllegalStateException`.

**Oracle :**  
Le test vérifie :

`assertFalse(CharsetUtils.isSupported("x-test-only-charset"))`

Le comportement attendu est `false`, car une erreur dans le mécanisme ICU ne doit pas faire considérer le charset 
comme supporté.

Ce test cible le mutant situé dans le chemin de gestion d'exception de `isSupported`.

#### testForNameUsesIcuHookWhenAvailable

**Intention :**  
Vérifier que `CharsetUtils.forName()` retourne correctement le charset fourni par le hook ICU lorsque Java ne reconnaît 
pas directement le nom demandé.

**Données de test :**  
La chaîne `"x-test-only-charset"` est encore utilisée afin de forcer le passage par le mécanisme ICU.

Le faux hook `getCharsetICU` retourne volontairement `StandardCharsets.UTF_16BE`.

**Oracle :**  
Le test utilise `assertSame` pour vérifier que l'objet retourné par `CharsetUtils.forName()` est exactement celui 
fourni par le hook ICU.

Le résultat attendu est donc `StandardCharsets.UTF_16BE`.

Ce test cible les mutants liés au retour du charset obtenu par le mécanisme ICU.

### 5.2. Résultat après les tests manuels de CharsetUtils

Après l'ajout de ces trois tests, une nouvelle analyse PIT a été exécutée.

Les résultats sont passés de :

- **22/28 mutants tués = 79 %**

à :

- **27/28 mutants tués = 96 %**
- Line coverage : **74/92 = 80 %**
- Mutants sans couverture : **1**
- Test strength : **100 %**

Les 5 mutants non couverts de `CharsetUtils` ont donc tous été détectés par les trois tests manuels.

### 5.3. Test manuel pour ServiceLoaderUtils

Après cette étape, un seul mutant restait non détecté dans `ServiceLoaderUtils.newInstance()`.

#### testNewInstanceUsesServiceLoaderConstructor

**Intention :**  
Vérifier que `ServiceLoaderUtils.newInstance()` utilise correctement un constructeur prenant un `ServiceLoader` en 
paramètre lorsqu'un tel constructeur est disponible.

**Données de test :**  
Une classe auxiliaire `WithServiceLoaderConstructor` a été définie avec uniquement le constructeur suivant :

`WithServiceLoaderConstructor(ServiceLoader loader)`

Aucun constructeur sans argument n'a été défini. Cela force donc `ServiceLoaderUtils.newInstance()` à utiliser le chemin 
correspondant au constructeur avec `ServiceLoader`.

Une valeur `null` peut être utilisée pour le paramètre `ServiceLoader`, car le but du test est de vérifier le choix du 
constructeur et la création de l'objet, et non le comportement interne du loader.

**Oracle :**  
Le test utilise :

`assertNotNull(instance)`

Le programme original retourne l'instance créée par le constructeur. Le mutant restant remplaçait cette valeur de 
retour par `null`.

Ainsi :

- programme original → l'instance n'est pas `null`;
- mutant → le résultat devient `null` et le test échoue.

Ce test permet donc de tuer directement le dernier mutant restant.

## 6. Résultats finaux

Les résultats obtenus au cours des différentes étapes sont résumés dans le tableau suivant :

| Étape | Tests exécutés | Mutants tués | Mutation coverage | Mutants sans couverture |
|---|---|---|---|---|
| Tests originaux | 749 | 22/28 | 79 % | 6 |
| Tests originaux + tests générés par ChatUniTest | 771 | 22/28 | 79 % | 6 |
| + 3 tests manuels pour `CharsetUtils` | 774 | 27/28 | 96 % | 1 |
| + 1 test manuel pour `ServiceLoaderUtils` | 775 | 28/28 | 100 % | 0 |

La couverture de lignes des deux classes sélectionnées a également progressé :

- avant les tests manuels : **68/92 = 74 %**
- après les trois tests manuels de `CharsetUtils` : **74/92 = 80 %**
- résultat final : **75/92 = 82 %**

Le résultat final de PIT est donc :

- Mutants générés : **28**
- Mutants tués : **28**
- Mutation coverage : **100 %**
- Mutants sans couverture : **0**
- Test strength : **100 %**

Le dernier test complet du module `tika-core` a également réussi :

- Tests exécutés : **775**
- Failures : **0**
- Errors : **0**
- Skipped : **3**
- Résultat : **BUILD SUCCESS**

Ces résultats montrent que les tests générés par ChatUniTest ont augmenté le nombre de tests exécutés, 
mais n'ont pas amélioré le score de mutation. L'amélioration du score de mutation provient uniquement des 4 tests 
ajoutés manuellement et conçus spécifiquement pour cibler les mutants encore non détectés.

## 7. Conclusion

Cette tâche nous a permis de comparer l'efficacité de tests générés automatiquement par ChatUniTest avec celle de tests 
conçus manuellement.

