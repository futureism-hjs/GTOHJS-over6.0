# GTOHJS Dev11 Development Rules

Read this file and the workspace AGENT.md / AGENTS.md before each run.
Read README_EN.md and, when present, README_codex.md before development.
README.md is the default Chinese user-facing document; do not read it during
development. Read the local docs/INJECTION_FEASIBILITY.md, docs/DEVELOPMENT.md
and docs/READ_INDEX.md when available in the active development workspace.

- This is the active dev11 adaptation project. Old GTOHJS and all upstream
  GTOCore/GTOLib/Seal references remain read-only.
- Never use DSH, its executable, bridge or tools for this task.
- Restore every old feature without an equivalent dev11 replacement.
  Missing features remain incomplete.
- Select injection mechanisms from actual dev11 source and original-bytecode
  evidence. The user confirmed the current dev11 client tests passed.
- Use Java 21 and network-enabled ./gradlew clean build through IDEA MCP.
  Stop on network/dependency failure; never switch to offline.
- Write or edit source and documentation only through Codex patches.
- Preserve original Core/Seal JARs. Deploy only this addon after a successful
  build, backing up any prior addon first.
- Maintain English development documentation, feature matrix and read index.
  Static verification is not release-runtime acceptance.
- Client acceptance for GTOHJS changes consists only of entering a world in the
  target client and checking whether the game crashes. Successful world entry
  without a crash passes the client test. Do not extend this gate to gameplay
  checks of blocks, structures, interfaces, recipes or reload behavior. Leave
  the game running after a successful client test.

## Documentation and publication

- README.md is the default Chinese product README. README_EN.md is its English
  counterpart and is read during development. Keep both descriptions aligned
  with the previous project's feature-oriented README style.
- README_codex.md is local-only, is read during development, and contains
  machine-specific absolute paths. Never commit or upload it.
- Keep local development records, indexes, raw logs, caches, backups, diagnostics,
  credentials and connection files out of Git. Use the old project's ignore
  conventions, including *_codex.md. A .gitignore does not untrack existing files:
  inspect the actual proposed commit before every upload.
- The user has explicitly authorized uploading every completed change to
  https://github.com/futureism-hjs/GTOHJS-over6.0.git. Commit and push the scoped,
  sanitized source/documentation changes after applicable build, deployment
  and documentation work. No separate push confirmation is required.
- Use normal non-forced pushes. Inspect remote history before initialization or
  synchronization; preserve unrelated remote changes. Report an upload failure
  honestly and retain the local changes for retry.
- Upload source and public documentation only. Do not upload development JARs,
  third-party dependencies, private reference trees or local acceptance evidence.
  Release publication remains a separate action requiring explicit instruction.
- Do not use DSH for publication or any other work in this project.
