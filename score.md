### Calcul de score

`CalculateurScore.java`

Pour chaque carte :

- Si hauteur = 2 à 10 → ajouter la valeur nominale
- Si hauteur = Valet, Dame, Roi → ajouter 10
- Si hauteur = As → mettre de côté (compter à part)

**Astuce** : Créez deux variables :

- `scoreBase` : somme sans les As
- `nombreAs` : compteur d'As

### **Le problème des As : 1 ou 11 ?**

**Question clé** : Un As vaut 1 OU 11. Comment choisir ?

**Règle d'or** :

> Un As vaut **11 si ça ne fait pas dépasser 21**, sinon il vaut **1**.

**Exemple** :

Main 1 : [As, 7]
scoreBase = 7
nombreAs = 1

Si As = 11 → 7 + 11 = 18 ✅ (ne dépasse pas 21)
Si As = 1 → 7 + 1 = 8 (moins bon)
→ Choisir 18

Main 2 : [As, 7, 5]
scoreBase = 7 + 5 = 12
nombreAs = 1

Si As = 11 → 12 + 11 = 23 ❌ (dépasse 21)
Si As = 1 → 12 + 1 = 13 ✅
→ Choisir 13

---

### **Algorithme pour gérer PLUSIEURS As**

**Cas complexe** : Main avec 2 ou 3 As

Main : [As, As, 9]
scoreBase = 9
nombreAs = 2

Possibilités :

- As1=11, As2=11 → 9 + 11 + 11 = 31 ❌
- As1=11, As2=1 → 9 + 11 + 1 = 21 ✅ Optimal !
- As1=1, As2=1 → 9 + 1 + 1 = 11

**Stratégie optimale** :

1. Par défaut, tous les As valent **1**
2. Essayer de transformer **un seul As** en 11
3. Si ça ne dépasse pas 21, garder cette valeur
4. Sinon, tous les As restent à 1

**Algorithme** :

```
scoreTotal = scoreBase + (nombreAs × 1) // Tous les As = 1

Si nombreAs > 0 :
Si scoreTotal + 10 ≤ 21 : // Transformer un As de 1 en 11 (+10)
scoreTotal += 10

```

**Pourquoi +10 ?**

- Un As qui passe de 1 à 11 ajoute 10 de plus
- On a déjà compté 1, donc on ajoute juste 10

---

### **Cas particulier : Blackjack naturel**

**Définition** : As + Figure/10 en **exactement 2 cartes** = Blackjack

**Vérification** :

```

Conditions :

1. La main a exactement 2 cartes
2. Le score total = 21
3. Une des cartes est un As
4. L'autre carte vaut 10 (10, Valet, Dame, Roi)

```

**Pourquoi vérifier ?**

- Blackjack paie 1.5× (au lieu de 1×)
- [As, Roi] = Blackjack → 1.5× mise
- [7, 7, 7] = 21 mais PAS Blackjack → 1× mise

---

### 6️⃣ **Structure suggérée pour la classe**

**Méthodes à créer** :

```

1. calculerScore(Paquet main) : int

   - Méthode principale
   - Retourne le meilleur score possible ≤ 21
   - Si tous les scores possibles dépassent 21, retourne le plus petit

2. aDepasse(Paquet main) : boolean

   - Retourne true si score > 21
   - Raccourci : calculerScore(main) > 21

3. estBlackjack(Paquet main) : boolean

   - Vérifie les 4 conditions du Blackjack naturel

4. estAsSouple(Paquet main) : boolean (optionnel mais utile)
   - Retourne true si la main contient un As qui compte pour 11
   - Utile pour l'affichage (ex : "18 souple")

```

---

### 7️⃣ **Pseudo-code de calculerScore()**

```

FONCTION calculerScore(main)
scoreBase = 0
nombreAs = 0

    POUR chaque carte DANS main :
        hauteur = carte.getHauteur()

        SI hauteur est entre DEUX et DIX :
            scoreBase += valeur nominale
        SINON SI hauteur est VALET, DAME ou ROI :
            scoreBase += 10
        SINON SI hauteur est AS :
            nombreAs += 1
            scoreBase += 1  // Provisoirement, As = 1

    // Maintenant, essayer de transformer un As en 11
    SI nombreAs > 0 ET scoreBase + 10 <= 21 :
        scoreBase += 10  // Un As passe de 1 à 11

    RETOURNER scoreBase

FIN FONCTION

```
