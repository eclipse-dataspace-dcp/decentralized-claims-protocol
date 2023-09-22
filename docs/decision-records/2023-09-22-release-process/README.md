# Defining a simple release process

## Decision

The release process for the specifications should be simple and easy to understand. We will not follow GitFlow, but
employ a simple tag-based approach.

## Rationale

With code artefacts it is oftentimes required to be able to back-port bugfixes or even features, or maintain older
versions. Here, the deliverable is merely a compressed archive with static non-code content, so the release process can
be kept fairly simple and linear.

## Approach

Every release will be tagged on the `main` branch using semantic versioning, starting at `0.1.0`. Then,
using that tag, a GitHub Release is to be created, for example using the `ncipollo/release-action` GitHub action.
Finally, a compressed archive containing the current specification documents will be uploaded as artefact to the
GitHub release.

## Further notes

Even though it is common to create a separate branch containing just the release commit (often named `releases`), there
is no apparent advantage over the tag-only approach. 