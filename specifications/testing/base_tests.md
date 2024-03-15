# IATP Test Plan - Presentation Flow

<!-- TOC -->
* [IATP Test Plan - Presentation Flow](#iatp-test-plan---presentation-flow)
  * [Introduction](#introduction)
  * [Definition of terms](#definition-of-terms)
  * [Verifying LDP-VC / LDP-VP](#verifying-ldp-vc--ldp-vp)
  * [Validating LDP VCs and VPs](#validating-ldp-vcs-and-vps)
  * [Verifying JWTs](#verifying-jwts)
  * [Validating JWT VCs/VPs](#validating-jwt-vcsvps)
  * [Validating SI tokens as the relying party](#validating-si-tokens-as-the-relying-party)
  * [Validating SI tokens in the CredentialService](#validating-si-tokens-in-the-credentialservice)
  * [End-To-End tests](#end-to-end-tests)
  * [Implementer's notice: certification of compliance](#implementers-notice-certification-of-compliance)
    * [1. Via test annotations](#1-via-test-annotations)
    * [1. Embedding in client code](#1-embedding-in-client-code)
    * [2. Remote execution of tests](#2-remote-execution-of-tests)
  * [Future amendments](#future-amendments)
  * [References](#references)
<!-- TOC -->

## Introduction

This document outlines a series of tests that various participants in an identity network based on IATP must pass. Such
a system typically consists of two participants, where one party proves to the other party a series of claims. Those
claims are represented as a series of VerifiableCredentials, which the proving party presents in the form of a
VerifiablePresentation. The relying party then verifies those claims against or with the help of established
cryptographic primitives and systems.
Where applicable, this document also provides test vectors in the form of `*.json` files.

## Definition of terms

For the purposes of this document, term definitions from
the [IATP specifications](http://github.com/eclipse-tractusx/identity-trust) apply, unless stated otherwise. In
addition, the following terms apply:

- proving party: the party that makes certain claims about themselves
- relying party: the party that wants to verify a certain claim
- LDP: Linked Data Proof, synonymous for VCs/VPs in the JSON-LD format
- JWT-VC, JWT-VP: VCs/VPs in the Json Web Token format
- Verification: the process of ensuring the cryptographic validity of a previously signed data structure.
- Validation: the process of ensuring that a data structure is in an acceptable form. This can involve checking that
  certain properties are there, that values fall within an acceptable range, etc. Validation may be dependent on other
  data structures.
- Context: refers to the `@context` section of a JSON-LD document

## Verifying LDP-VC / LDP-VP

| Number | Name                                                             | Description                                                                                                                                                                                                                                                                                                                                                                                        | Sample data                                                                                                                           |
|--------|------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| V_0001 | Valid VC with linked proof                                       | A valid VerifiableCredential in JSON-LD format containing a linked proof object is successfully verified                                                                                                                                                                                                                                                                                           | [v_0001_valid_vc_linked_proof.json](sample_input_verification/v-t_0001_valid_vc_linked_proof.json)                                    |
| V_0002 | Valid VC with embedded proof                                     | A valid VerifiableCredential in JSON-LD format containing an embedded proof object is successfully verified                                                                                                                                                                                                                                                                                        | [v_0002_valid_vc_embedded_proof.json](sample_input_verification/v-t_0002_valid_vc_embedded_proof.json)                                |
| V_0003 | Detect a forged claim                                            | An entry in the `credentialSubject` object was _modified_. The test subject must detect that and report a verification error.                                                                                                                                                                                                                                                                      | [v_0003_vc_embedded_forged_claim.json](sample_input_verification/v-t_0003.a_vc_embedded_forged_claim.json).                           |
| V_0004 | Detect an altered claim (removal)                                | An entry in the `credentialSubject` object was _removed_. The test subject must detect that and report a verification error.                                                                                                                                                                                                                                                                       | [v_0004_vc_embedded_altered_credentialsubject.json](sample_input_verification/v-t_0003.b_vc_embedded_altered_credentialsubject.json). |
| V_0005 | Detect an altered claim (alteration).                            | Any other claim, e.g. `issuer`, `expirationDate`,... was modified. The test subject must detect that and report a validation error. Note that this test case may consist of several sub-testcases, one per claim that was altered/removed. So at runtime, this test case may get invoked several times.                                                                                            | [v_0005_vc_embedded_altered_claim.json](sample_input_verification/v-t_0003.c_vc_embedded_altered_claim.json).                         |
| V_0006 | Detect a tampered/modified proof object.                         | The `proof` object was modified, for example, by altering the `proofPurpose`. The test subject must detect that and report a verification error. Note that this test case may consist of several sub-testcases, one per addition, modification or removal. So at runtime, this test case may get invoked several times.                                                                            | [v_0006_vc_embedded_forged_proof.json](sample_input_verification/v-t_0004.a_vc_embedded_forged_proof.json).                           |
| V_0007 | Detect an invalid proof.                                         | In particular, the mathematical properties of the proof are invalid. Depending on the cryptographic primitives that are used, this could be an invalid curve coordinate (EC keys, Octet Key Pairs), tampered exponent, factors or prime values (RSA keys). (Embedded) proofs are represented in JWK format. Test subjects must detect that and report an error that is _not_ a verification error. | [v_0007_vc_embedded_invalid_proof.json](sample_input_verification/v-t_0004.b_vc_embedded_invalid_proof.json)                          |
| V_0008 | Verify canonicalization by altering the sequence of the JSON-LD. | An otherwise valid JSON-LD is changed in _ordering_. Some entries in the VC as well as within the `proof` object have been re-ordered. The test subject should verify the document successfully.                                                                                                                                                                                                   | [v_0008_vc_canonicalization.json](sample_input_verification/v-t_0005_vc_canonicalization.json).                                       |
| V_0009 | Verify a VC with a proof set                                     | A valid VerifiableCredential contains a ["Proof set"](https://w3c.github.io/vc-data-integrity/#proof-sets) (mixture of embedded and linked), where the sequence does not matter. All proofs are valid, at least one is embedded and at least one is linked. Their sequence does not matter, all proofs assert the same VC.                                                                         |                                                                                                                                       |
| V_0010 | Verify a VC with a proof chain                                   | Verify a VerifiableCredential that contains a ["Proof chain"](https://w3c.github.io/vc-data-integrity/#proof-chains), that asserts the _order_ of proofs.                                                                                                                                                                                                                                          |                                                                                                                                       |
| V_0011 | Detect one or more forged proofs in a Proof Set                  | A VerifiableCredential contains a Proof Set with one or several invalid proofs (mixture of embedded and linked). The test subject must report a verification error.                                                                                                                                                                                                                                |                                                                                                                                       |
| V_0012 | Detect invalid order in a Proof Chain.                           | A VerifiableCredential contains a Proof Chain with a cyclic reference.                                                                                                                                                                                                                                                                                                                             |                                                                                                                                       |
| V_0013 | Verify a valid VP.                                               | Verify a valid VerifiablePresentation containing one or more VCs.                                                                                                                                                                                                                                                                                                                                  | [v_0013_vp_compacted.json](sample_input_verification/v-t_0010_vp_compacted.json).                                                     |
| V_0014 | Detect a forged VP proof (ov er valid VCs).                      | A VP contains one or several valid VCs, but the VP proof is invalid (= spoofed).                                                                                                                                                                                                                                                                                                                   |                                                                                                                                       |
| V_0015 | Detect a valid VP containing an invalid VC                       | The VP is valid, but was generated over `[1, 2, N]` invalid VCs. The VP proof is valid, but the contained VCs are invalid. The test subject must report a validation error.                                                                                                                                                                                                                        |                                                                                                                                       |

## Validating LDP VCs and VPs

| Number | Name                                            | Description                                                                                                                                                                                         | Sample Data                                                                                                                                             |
|--------|-------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| V_0016 | VC `type` must contain `"VerifiableCredential"` | The `type` field cannot be empty and must contain at least the `VerifiableCredential` type, otherwise the test subject rejects it.                                                                  | [v_0016_vc_missing_type](https://github.com/w3c/vc-test-suite/blob/gh-pages/test/vc-data-model-1.0/input/example-3-bad-missing-type.jsonld)             |
| V_0017 | `credentialSubject` is present (object)         | The validator accepts a VC, in which the `credentialSubject` field is present and is a single object                                                                                                | [v_0017_single_sub](https://github.com/w3c/vc-test-suite/blob/gh-pages/test/vc-data-model-1.0/input/example-1.jsonld)                                   |
| V_0018 | `credentialSubject` is present (array)          | The validator accepts a VC, in which the `credentialSubject` field is present and is an array                                                                                                       | [v_0018_array_sub](https://github.com/w3c/vc-test-suite/blob/gh-pages/test/vc-data-model-1.0/input/example-014-credential-subjects.jsonld)              |
| V_0019 | `credentialSubject` is missing                  | The validator rejects a VC that does not contain a `credentialSubject`                                                                                                                              |                                                                                                                                                         |
| V_0020 | `issuer` is not present                         | The `issuer` property is missing, the test subject rejects the VC                                                                                                                                   | [v_0020_missing_issuer](https://github.com/w3c/vc-test-suite/blob/gh-pages/test/vc-data-model-1.0/input/example-4-bad-missing-issuer.jsonld)            |
| V_0021 | `issuer` is not a URI                           | The `issuer` must be a single URI.                                                                                                                                                                  | [v_0021_issuer_format](https://github.com/w3c/vc-test-suite/blob/gh-pages/test/vc-data-model-1.0/input/example-4-bad-issuer-uri.jsonld)                 |
| V_0022 | `issuer` is an array                            | The `issuer` property contains multiple elements.                                                                                                                                                   | [v_0022_issuer_multi](https://github.com/w3c/vc-test-suite/blob/gh-pages/test/vc-data-model-1.0/input/example-4-bad-issuer-cardinality.jsonld)          |
| V_0023 | `issuanceDate` is RFC3339 date time             | The validator accepts a VC that contains an `issuanceDate` that is a valid RFC3339 date time. If the `issuanceDate` is missing, if it is an array, or if it is not valid RFC3339 the VC is rejected | [v_0023_issdate_valid](https://github.com/w3c/vc-test-suite/blob/gh-pages/test/vc-data-model-1.0/input/example-4.jsonld)                                |
| V_0024 | `expirationTime` is RFC3339 date time           | The validator accepts a VC that contains an `expirationDate` that is a valid RFC3339 date time, or that is null. If it is present, but not valid RFC3339, or if it is an array, the VC is rejected. | [v_0024_expiration_valid](https://github.com/w3c/vc-test-suite/blob/gh-pages/test/vc-data-model-1.0/input/example-6.jsonld)                             |
| V_0025 | VP `type` must contain `VerifiablePresentation` | The `type` field cannot be empty and must contain at least the `VerifiablePresentation` type, otherwise the test subject rejects it.                                                                | [v_0025_presentation_type](https://github.com/w3c/vc-test-suite/blob/gh-pages/test/vc-data-model-1.0/input/example-8.jsonld)                            |
| V_0026 | VP does not contain `proof` object`             | The validator accepts a VP that does not contain a `proof` object.                                                                                                                                  |                                                                                                                                                         |
| V_0027 | VP contains `proof` but no `proof.type`         | A VP, that contains a `proof` object, but the proof does not contain the `type` field, is rejected.                                                                                                 | [v_0027_proof_without_type](https://github.com/w3c/vc-test-suite/blob/gh-pages/test/vc-data-model-1.0/input/example-8-bad-missing-proof-type.jsonld)    |
| V_0028 | VP contains no `verifiableCredential`           | A VP with an empty `verifiableCredentials` object is accepted                                                                                                                                       | [v_0028_vp_without_vc](https://github.com/w3c/vc-test-suite/blob/gh-pages/test/vc-data-model-1.0/input/example-017-missing-verifiableCredential.jsonld) |

## Verifying JWTs

Although technically it does not involve any cryptographic operation, it must be made sure that the basic formal
structure of the JWT standard is adhered to. So evey verification/validation operation must implicitly validate that,
otherwise subsequent steps are not possible. Note that this section is not specific to VC/VP JWTs. Refer
to [the JWT specification, section 7.2](https://datatracker.ietf.org/doc/html/rfc7519#section-7.2).

| Number | Name                                  | Description                                                                                                                                                                                                           |
|--------|---------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| J_0001 | Assert `typ` header                   | Reject any JWT where the `typ` header is not `JWT`                                                                                                                                                                    |
| J_0002 | Reject JWTs with invalid `alg` header | Reject any JWT where the value of the `alg` header is not known or understood, or the given algorithm is not supported by the application                                                                             |
| J_0003 | Reject unsecured JWTs                 | Reject any JWT where the `alg` header is set to `none`, and the signature portion of the base64 String is empty                                                                                                       |
| J_0004 | Accept only JWS JWTs                  | Reject any JWT that is not a JWS, e.g. JWE                                                                                                                                                                            |
| J_0005 | Reject JWTs that fail verification    | Reject any JWT where the signature cannot be verified. Note that resolving the public key may require additional implementation-specific checks, for example that an `iss` claim is present, and contains a valid DID |

## Validating JWT VCs/VPs

| Number | Name                                            | Description                                                                                                                  |
|--------|-------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------|
| J_0006 | Assert presence of mandatory claims             | Accept only JWTs, where all mandatory claims are present. These are: `aud`, `iss`, `sub`, `iat`, `exp`, `jti`                |
| J_0007 | Reject wrong `aud`                              | Reject any JWT where the `aud` claim does not match an expected value                                                        |
| J_0008 | VP: reject JWTs that do not contain `vp`        | Reject any JWT-VP that does not contain a `vp` claim                                                                         |
| J_0009 | VP: assert that objects and arrays are accepted | Assert, that the `vp` claim can contain both a single JSON object, or an array of objects                                    |
| J_0009 | VP: reject JWTs without `kid` header            | Reject any JWT-VP where the `kid` is not set, or where the value of the `kid` header is not understood                       |
| J_0010 | VP: reject expired JWTs                         | Reject any JWT-VP where the `exp` value is before the current time. Apply reasonable `epsilon` to account for clock skew.    |
| J_0011 | VP: reject invalid `vp` claim                   | Reject any JWT-VP where the `vp` claim cannot be parsed into a valid VerifiablePresentation                                  |
| J_0012 | VC: reject missing claims                       | Reject any JWT-VC, where the `exp`, `iss`, `nbf`, `jti`, `sub` or `aud` claims are not present                               | 
| J_0012 | VC: reject invalid claims                       | Reject any JWT-VC, where the `exp`, `iss`, `nbf`, `jti`, `sub` or `aud` do not match the corresponding values in the VC JSON | 
| J_0013 | VC: assert that objects and arrays are accepted | Assert, that the `vc` claim can contain both a single JSON object, or an array of objects                                    |

<!---
Might need to move this into another document
--> 

## Validating SI tokens as the relying party

This section outlines tests that need to be passed on the _relying side_, when receiving Self-issued ID
tokens that contain a `token`.
Please read details [here](./verifier.md)

## Validating SI tokens in the CredentialService

This sections outlines tests that the CredentialService needs to pass, when receiving back another
Self-issued ID token as Auth header on the `/presentation/query` endpoint.
Please read details [here](./credentialservice)

## End-To-End tests

To be written - this outlines tests, that view the CredentialService as a black box. For example, it would specify a
case
where the CredentialService receives

## Implementer's notice: certification of compliance

The following section outlines several ways how conformance with this test plan can be established.

### 1. Via test annotations

Assuming a JVM language, the implementor simply puts special annotations onto test methods to enable subsequent tooling
to generate conformance reports.

For example:

```java

@IatpTest(number = "J_0010")
@DisplayName("VP: reject expired JWT")
void verifyJwt_rejectExpired() {
    // test code
}

@IatpTest(number = "J_0009", disabled = true, reason = "Not supported in this version")
void verifyJwt_rejectJwt_withoutKidHeader() {
}
```

Be aware that since this is in essence a self-proclaimed conformance report, the implementation may be marked as having
limited compliance.

### 1. Embedding in client code

For implementations that are based on a JVM-language, a test framework will be provided, containing an SPI to which test
subjects can delegate test execution. Practically, implementors simply inherit a test base class that contains all test
code and that only delegates back to client code for business logic invocations, such as creating keys. Throwing
an `UnsupportedOperationException` in client code marks the test as "disabled".

For example, to verify the correct signing of a VC, the test framework would set up test data (i.e. a JSON-LD file
containing a VC) and possibly generate a keypair, and would pass the JSON-LD back into the test subject for signing. The
test subject signs it, and returns it to the test framework, where it is subsequently validated.

Tests are self-contained and quick to execute. In order to ensure continued and reproducible compliance, it is advisable
to execute test tests in a CI/CD environment.

### 2. Remote execution of tests

If the implementation is done in another programming language, or if the aforementioned approach is not suitable for
some other reason, a remote test execution framework may be implemented, where subjects-under-test implement a simple
and standardized REST API, over which it receives the test data, invokes the appropriate methods, and then sends back
the results to the (remote) test framework.

Returning an HTTP 204 No Content marks the test as "disabled".

That way, the implementation of the tests and the implementation of the business code is completely decoupled: tests
still run within the test framework, but the test subject's implementation runs in its own process.

This approach provides two major advantages:

- test certification: the test code (in the test framework) can be certified and developed individually. Changes there
  won't affect the client code.
- portability: running test framework compliance tests could be done in CI, or even as a `helm test`
- the test framework can record test runs in its system-of-records for future reference

## Future amendments

## References

1. [Verifiable Credential Data Model](https://www.w3.org/TR/vc-data-model/#identifiers)
2. [W3 VC Test Suite](https://github.com/w3c/vc-test-suite)