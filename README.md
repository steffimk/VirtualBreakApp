# VirtualBreak

Willkommen bei VirtualBreak!

Diese App entstand im Rahmen des Praktikum Mobile und Verteilte Systeme (MSP) der LMU im WS20/21 unter der Leitung von Prof. Dr. Claudia Linnhoff-Popien, Steffen Illium und Andreas Sedlmeier

Viel Spaß in gemeinsamen virtuellen Pausen wünscht MSP Gruppe 4: Manuela Eska, Miriam Halsner, Stefanie Kloß, Jenny Phu

Diese App benutzt
- Firebase Authentication,
- Firebase Realtime database für die User, Gruppen, Freunde und Pausenraumdaten, für Game, Sportraum und Chat
- Firebase Storage (für Profilbilder) und
- Firebase Cloud Messaging für Benachrichtigungen

Die VideocallActivity wurde mit Jitsi Meet erstellt: https://jitsi.github.io/handbook/docs/dev-guide/dev-guide-android-sdk

Es gibt verschiedene Arten von Pausenräumen: Kaffee (mit BoredAPI), Sport (mit Fitness Timer), Spiel (mit Hangman Game) und Frage (mit Möglichkeit, Text zu pinnen)

Die Navigation innerhalb der App (nach Login) erfolgt hauptsächlich über den Navigation Component: app/main/res/navigation/mobile_navigation.xml über Fragments

Features:
- Main, Signup, LoginActivity
- Gruppen und Freundeliste
- Freunde hinzufügen über Email, Freundschaftsanfragen annehmen, ablehnen und anzeigen
- Gruppen und Pausenräume erstellen und verlassen
- In Pausenräumen verschiedene Aktivitäten, Chat und Videocall
- Profil und Profilbilder
- Status
- Benachrichtigungen über neue Gruppen, Pausenräume, Freunde
- Impressum
- Floating Widget wenn in Pause


