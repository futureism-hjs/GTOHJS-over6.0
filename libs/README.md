# Local Build Dependencies

Place the target dev9 dependency JARs in this directory before building a
standalone checkout. They are compile-only references, are ignored by Git and
are not redistributed in GTOHJS.

Required flat-directory artifacts match the aliases in build.gradle:

- gtocore-forge-1.20.1-26.9.5.jar (original dev9 Core)
- gtceu-1.20.1-forge-1.20.1-26.9.70.jar (GTM embedded in the original Core)
- datasynclib-forge-1.20.1-26.9.4.jar
- ldlib-forge-1.20.1-1.0.52.a.jar
- appliedenergistics2-forge-1.20.1-15.269.3.jar
- fastrecipesearch-1.20.1-26.8.4-forge.jar
- RecipeSearch-26.8.4.jar (nested library in that FastRecipeSearch artifact)
- Botania-1.20.1-456-FORGE.jar
- emi-1.1.24+1.20.1+forge.jar
- configuration-forge-1.20.1-3.1.0.jar

Preserve original Core/Seal files. Embedded dependency extraction uses 7-Zip;
do not substitute a different GTM or RecipeSearch version. FastCollection,
Kotlin, Forge and compiler dependencies resolve online through Gradle.

An ignored local.properties may define gtohjs.referenceDirs as a pipe-separated
list of extra directories. Machine-specific locations are documented only in
the local-only README_codex.md. Public checkouts default to portable libs
placement. Use Java 21 and exactly ./gradlew clean build with network access.
