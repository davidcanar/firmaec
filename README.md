# FirmaEC (reconstructed source)

Unofficial, reconstructed source code for **FirmaEC Firmador 5.3.0**, the Ecuadorian digital signature desktop application.

> **Disclaimer:** This is **not** the official source code repository. The code here was reconstructed by decompiling the officially distributed binary `firmador-jar-with-dependencies.jar` (version 5.3.0) and fixing decompiler artifacts so it compiles cleanly. The official application and its authors remain at [firmadigital.gob.ec](https://www.firmadigital.gob.ec). The original sources are hosted (privately) at `minka.gob.ec/mintel/ge/firmaec`. "FirmaEC" and its trademarks belong to their respective owners; this repository is a community reconstruction made possible by the project's GPLv3 license.

## What was modified

This reconstruction includes the following functional changes on top of the original 5.3.0 behavior:

1. **Offline startup** — the version check against `api.firmadigital.gob.ec` no longer kills the app when the service is unreachable; the app starts in local mode instead.
2. **Offline signing** — signing no longer aborts when the network is unavailable:
   - server time falls back to the local clock,
   - CRL/OCSP revocation checks are skipped (best-effort) instead of being fatal.
   When online, full server-side behavior is preserved.
3. **Remember certificate preferences** — the "Archivo"/"Token" selection and the certificate file path are remembered between launches (never the password, never the documents).

## Building

Requires JDK 17 and Maven (portable copies work fine):

```powershell
$env:JAVA_HOME = "<path-to-jdk-17>"
mvn package -DskipTests
```

The ready-to-run jar is produced at `firmador/target/firmador-jar-with-dependencies.jar`.

`rebuild.ps1` automates build + copy to a test folder (paths are machine-specific, adjust as needed).

## Repository layout

```
firmadigital/
├── pom.xml          parent POM (ec.gob.firmadigital:firmadigital:5.3.0)
├── libreria/        signing library (decompiled 5.3.0 sources)
└── firmador/        desktop signer UI (decompiled 5.3.0 sources)
```

## License

This project is licensed under the **GNU General Public License v3** — the same license declared by the original artifacts. Original copyright belongs to the FirmaEC authors (Ministerio de Telecomunicaciones, Soporte Libre, SERCOP). See [LICENSE](LICENSE).
