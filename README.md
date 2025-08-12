# Sprint Framework - Projet Maven Multi-Modules

## 🏗️ Structure du Projet

Ce projet a été restructuré en un projet Maven multi-modules avec la structure suivante :

```
projet-parent/
├── framework/          # Module du framework maison
├── demo/              # Module de démonstration
├── pom.xml            # POM parent
└── deploy.bat         # Script de déploiement
```

## 📁 Modules

### 1. Framework (`framework/`)
- **Contient** : Le code source du framework maison
- **Structure** : `src/main/java/com/sprint/`
- **Package** : `com.sprint.*`
- **Type** : JAR (bibliothèque)

### 2. Demo (`demo/`)
- **Contient** : Les projets de démonstration et tests
- **Structure** : `src/main/java/com/sprint/demo/`
- **Dépendance** : Framework (via Maven)
- **Type** : WAR (application web)

## 🚀 Démarrage Rapide

### Prérequis
- Java 21+
- Maven 3.9+
- Tomcat (testé avec XAMPP)

### 1. Construction complète
```bash
mvn clean package
```

### 2. Déploiement automatique
```bash
.\deploy.bat
```

### 3. Accès à l'application
- **URL principale** : http://localhost:8080/sprint-demo/
- **Routes disponibles** :
  - `/hello` - Page de démonstration
  - `/user` - Formulaire utilisateur
  - `/login` - Page de connexion

## 🔧 Commandes Maven

### Construire tous les modules
```bash
mvn clean package
```

### Construire un module spécifique
```bash
cd framework && mvn clean package
cd demo && mvn clean package
```

### Tests
```bash
mvn test
```

## 📋 Configuration

### POM Parent (`pom.xml`)
- Gère les versions communes
- Déclare les modules
- Centralise les dépendances

### POM Framework (`framework/pom.xml`)
- Configuration JAR
- Dépendances du framework

### POM Demo (`demo/pom.xml`)
- Configuration WAR
- Dépendance vers le framework
- Plugin Tomcat (optionnel)

## 🎯 Exemple d'utilisation

### Classe de démonstration
```java
package com.sprint.demo;

import com.sprint.annotation.GET;
import com.sprint.annotation.url;
import com.sprint.framework.ModelView;

@url("/hello")
public class DemoController {

    @GET
    public ModelView hello() {
        ModelView mv = new ModelView("hello.jsp");
        mv.addObject("message", "Hello from Sprint Framework!");
        return mv;
    }
}
```

## 🔄 Déploiement Manuel

### Méthode 1 : Script automatique
```bash
.\deploy.bat
```

### Méthode 2 : Manuel
```bash
cd demo
mvn clean package
copy target\sprint-demo.war C:\xampp\tomcat\webapps\
```

## 🐛 Dépannage

### Erreurs courantes
1. **Port 8080 occupé** : Vérifiez qu'aucune autre application n'utilise ce port
2. **Erreur 404** : Vérifiez que Tomcat est démarré et que l'application est bien déployée
3. **Erreur de compilation** : Assurez-vous que Java 21+ est installé

### Vérifier le déploiement
1. Vérifiez la présence de `sprint-demo.war` dans `webapps/`
2. Vérifiez que le dossier `sprint-demo/` est créé
3. Consultez les logs Tomcat : `logs/catalina.out`

## 📞 Support

Pour toute question ou problème, consultez :
- Le fichier `DEPLOYMENT_GUIDE.md`
- Les logs Tomcat
- La documentation du framework
