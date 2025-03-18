## Profiles of the Decentralized Claims Protocol

Different variations of the Verifiable Credentials Data Model, the revocation system, and proof stack
complicate interoperability. Profiles of the DCP specification help
narrow down those possibilities.

This specification defines two profiles, which are referenced by an alias.

## DCP Profile Definitions

| alias             | VC Data Model                                                | Revocation System                                                        | Proof Stack                                                                      | Remarks                                                                                                                                                                                                           |
|-------------------|--------------------------------------------------------------|--------------------------------------------------------------------------|----------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `vc20-bssl/jwt`   | [VC Data Model 2.0](https://www.w3.org/TR/vc-data-model-2.0) | [BitStringStatusList](https://www.w3.org/TR/vc-bitstring-status-list/)   | Enveloped proofs [using JWT/JOSE](https://www.w3.org/TR/vc-jose-cose/#with-jose) | Ignore `ttl`, use `validUntil` (*). No JWE supported. The concrete signature algorithm is not specified, as implementors are expected to handle all reasonably well-known crypto algorithms like RSA, EC and EdDSA |
| `vc11-sl2021/jwt` | [VC Data Model 1.1](https://www.w3.org/TR/vc-data-model)     | [StatusList2021](https://www.w3.org/TR/2023/WD-vc-status-list-20230427/) | External proofs using JWT                                                        | No JWE supported. The concrete signature algorithm is not specified, as implementors are expected to handle all reasonably well-known crypto algorithms like RSA, EC and EdDSA                                    |

(*): In its current form, the BitStringStatusList credential data
model [conflicts](https://www.w3.org/TR/vc-bitstring-status-list/#bitstringstatuslistcredential) with the VC DataModel
2.0, specifically regarding the validity period (`ttl` vs `validUntil`).

### Homogeneity requirement

Verifiable Credentials MUST be _homogenous_. This means the
same data model version and proof mechanism must be used for both credentials and presentations. For example, a
verifiable credential secured with an _enveloped proof_ using
JOSE ([VC Data Model 2.0](https://www.w3.org/TR/vc-jose-cose/#with-jose)) is enclosed in a verifiable presentation 
that is also secured with JWT/JOSE.

Heterogeneous sets of credentials MUST be enclosed in multiple presentations in such that each presentation only 
contains credentials of the same data model and proof mechanism.

## Profile Authoring Recommendations

This non-normative section is intended to provide guidance to authors who aim at defining their own profile definition.

For a usable profile, at least the following aspects must be defined:

- **Verifiable Credential Data Model**: defines structure, format, required attributes, including claims and verification methods.
- **Revocation System**: specifies how the validity and expiration of Verifiable Credentials is checked.
- **Proof stack**: how data integrity of the VC is to be provided.

In addition, it is possible to further constrain the profile, for example by limiting the number of acceptable
cryptographic algorithms.

Profile authors are recommended, when possible, to use a single credential format.
