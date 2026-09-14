package dev.umairalishah.ctsauth.model;

/**
 * Mirrors the actor roles used by the correspondence/document workflow in
 * the docmanager-api service: a CONTRIBUTOR registers documents, a REVIEWER
 * approves or rejects them, and an ADMIN manages users and the workflow.
 */
public enum Role {
    ADMIN,
    REVIEWER,
    CONTRIBUTOR
}

