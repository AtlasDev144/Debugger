package com.playarcanum;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class Debugger<Logger> {
    protected final Map<String, Section> activeSections;
    protected final Logger logger;

    protected final Consumer<String> debug;
    protected final Consumer<String> warning;
    protected final Consumer<String> error;

    /**
     * The constructor for a {@link Debugger}.
     * @param logger The logger instance to use
     * @param debug The logger's method to use for debug-level messages
     * @param warning The logger's method to use for warning-level messages
     * @param error The logger's method to use for error-level messages
     */
    protected Debugger(final Logger logger,
                       final Consumer<String> debug,
                       final Consumer<String> warning,
                       final Consumer<String> error) {
        this.logger = logger;
        this.debug = debug;
        this.warning = warning;
        this.error = error;
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
        this.activeSections.remove(sectionName);
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
        private final String name;
        private final DividerType dividerType;
        private final Logger logger;

        public Section(final int indentation, final String name, final DividerType dividerType) {
            this.name = name;
            this.dividerType = dividerType;
            this.logger = Debugger.this.logger;

            this.debug(this.dividerType.divider);
            this.debug("Debugger Section beginning");
        }

        protected void finish() {
            this.debug("Debugger Section complete");
            this.debug(this.dividerType.divider);
        }

        /**
         * Log a message.
         * @param message
         * @return
         */
        public Section debug(final String message) {
            Debugger.this.debug.accept("[" + this.name + "] " + message);
            return this;
        }

        /**
         * Print a warning.
         * @param message
         * @return
         */
        public Section warning(final String message) {
            Debugger.this.warning.accept("[" + this.name + "] " + message);
            return this;
        }

        /**
         * Print an error.
         * @param message
         * @return
         */
        public Section error(final String message) {
            Debugger.this.error.accept("[" + this.name + "] " + message);
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
