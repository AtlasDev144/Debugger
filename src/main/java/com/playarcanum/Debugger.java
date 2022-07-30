package com.playarcanum;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public abstract class Debugger {
    private final Map<String, Section> activeSections;
    protected final Logger logger;

    protected Debugger(final Logger logger) {
        this.logger = logger;
        this.activeSections = new HashMap<>();
    }

    /**
     * Creates a new {@link Debugger.Section}.
     * @param name
     * @param dividerType
     * @return
     */
    public Section create(final String name, final DividerType dividerType) {
        final Section result = new Section(this.activeSections.size(), name, dividerType);
        this.activeSections.put(name, result);
        return result;
    }

    /**
     * Returns the {@link Debugger.Section} for the given {@code name}, if it exists.
     * @param name
     * @return
     */
    public Optional<Section> section(final String name) {
        return Optional.ofNullable(this.activeSections.get(name));
    }

    /**
     * Finishes and removes a {@link Debugger.Section}.
     * @param sectionName
     */
    public void finish(final String sectionName) {
        this.section(sectionName).ifPresent(Section::finish);
        if(this.activeSections.remove(sectionName) != null) {
            //Adjust indentation 1 to the left since active sections decreased
            this.activeSections.forEach((name, section) -> {
                if(section.indentation > 0) {
                    section.indentation--;
                }
            });
        }
    }

    /**
     * Finishes and removes a {@link Debugger.Section}.
     * @param section
     */
    public void finish(final Section section) {
        this.finish(section.name);
    }

    /**
     * Allows easy segmenting and organization of multiple tasks that are currently being debugged.
     */
    public class Section {
        private int indentation;
        private final String name;
        private final DividerType dividerType;
        private final Logger logger;

        public Section(final int indentation, final String name, final DividerType dividerType) {
            this.indentation = indentation;
            this.name = name;
            this.dividerType = dividerType;
            this.logger = Debugger.this.logger;

            this.log(this.dividerType.divider);
            this.log("Debugger Section beginning");
        }

        protected void finish() {
            this.log("Debugger Section complete");
            this.log(this.dividerType.divider);
        }

        /**
         * Log a message.
         * @param message
         * @return
         */
        public Section log(final String message) {
            this.logger.fine("[" + this.name + "] " + message);
            return this;
        }

        /**
         * Print a warning.
         * @param message
         * @return
         */
        public Section warning(final String message) {
            this.logger.warning("[" + this.name + "] " + message);
            return this;
        }

        /**
         * Print an error.
         * @param message
         * @return
         */
        public Section error(final String message) {
            this.logger.severe("[" + this.name + "] " + message);
            return this;
        }
    }

    public enum DividerType {
        THICK("========================================"),
        THIN("----------------------------------------"),
        UNDERSCORE("________________________________________"),
        STAR("****************************************");

        private final String divider;

        DividerType(String div) { this.divider = div; }

        public String divider() { return this.divider; }
    }
}
