## Profiles of the Decentralized Claims Protocol


Many different variations of the VerifiableCredentials data model, the revocation system and the proof stack exist which
makes it almost impossible to reach a sufficient level of interoperability. Profiles of the DCP specification help
narrow down those possibilities.

This specification defines two profiles, which are referenced by an alias.

## DCP profile definitions

| alias             | VC data model                                               | revocation system                                                        | proof stack                                                                 | remarks                                                                                                                                                                                                           |
|-------------------|-------------------------------------------------------------|--------------------------------------------------------------------------|-----------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `vc20-bssl/jwt`   | [VC DataModel 2.0](https://www.w3.org/TR/vc-data-model-2.0) | [BitStringStatusList](https://www.w3.org/TR/vc-bitstring-status-list/)   | Enveloped proofs [using JWT](https://www.w3.org/TR/vc-jose-cose/#with-jose) | Ignore `ttl`, use `validUntil` *). No JWE supported. The concrete signature algorithm is not specified, as implementors are expected to handle all reasonably well-known crypto algorithms like RSA, EC and EdDSA |
| `vc11-sl2021/jwt` | [VC DataModel 1.1](https://www.w3.org/TR/vc-data-model)     | [StatusList2021](https://www.w3.org/TR/2023/WD-vc-status-list-20230427/) | External proofs using JWT                                                   | --"--                                                                                                                                                                                                             |

*) In its current form, the BitStringStatusList credential data
model [conflicts](https://www.w3.org/TR/vc-bitstring-status-list/#bitstringstatuslistcredential) with the VC DataModel
2.0, specifically regarding the validity period (`ttl` vs `validUntil`).

## Profile authoring recommendations

This non-normative section is intended to provide guidance to authors who aim at defining their own profile definition.

For a usable profile, at least the following aspects must be defined:

- VerifiableCredential Data Model
- Revocation System: specifies how the validity and expiration of VerifiableCredentials is checked
- Proof stack: how data integrity of the VC is to be provided

In addition, it is possible to further constrain the profile, for example by limiting the number of acceptable
cryptographic algorithms.