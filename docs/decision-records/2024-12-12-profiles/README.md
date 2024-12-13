# Interop Profiles for DCP

## Decision

DCP will define two interop profiles that constrain the VC data model, the revocation system and the proof stack.

## Rationale

In order to provide to implementors a way to establish interoperability without having to define a whole profile, DCP will specify two standard profiles. 
These profiles are the "sane defaults" for which DCP will also provide Technology Compatibility Kits.

## Approach

Profiles are defined using an _alias_ that should be indicative of what they contain:
- `vc20+bssl/jwt`: uses VC Data Model 2.0, BitStringStatusList as revocation system and enveloped proofs as JWT
- `vc11+sl2021/jwt`: uses VC Data Model 1.1, StatusList2021 as revocation system and external proofs in the form of JWT


In the specification document, the `CredentialObject` will be modified such that the `credentials[].format` and `credentials[].cryptography` properties get collapsed into the `credentials[].profile` property, which should contain the profile alias, e.g. `vc20+bssl/jwt`.

## Further notes

As an added bonus, the specification document should contain a non-normative text passage that outlines, which decisions authors of subsequent profiles need to make, i.e. the minimum set of things a profile descriptions should specify. 
