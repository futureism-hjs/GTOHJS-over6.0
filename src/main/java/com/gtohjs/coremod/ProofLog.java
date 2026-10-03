package com.gtohjs.coremod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Temporary cold-path evidence independent of redirected System.err. */
public final class ProofLog {
    private static final Logger LOGGER = LogManager.getLogger("GTOHJS Proof");
    private static final String EVIDENCE_PATH = System.getProperty("gtohjs.proof.evidence");

    private ProofLog() {}

    public static synchronized void record(String message) {
        LOGGER.error("[GTOHJS/PROOF] {}", message);
        if (EVIDENCE_PATH != null) {
            try {
                Files.writeString(Path.of(EVIDENCE_PATH), message + System.lineSeparator(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException exception) {
                throw new IllegalStateException("Cannot preserve GTOHJS proof evidence", exception);
            }
        }
    }
}
