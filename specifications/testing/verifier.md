# Verifying SI Tokens as "verifier"

In a typical scenario involving IATP, there is one party that wants to prove certain claims about themselves, and there
is one party that wants to verify those claims.

This document deals with tests regarding the _verifier_ side, sometimes also called the relying party.

## Test setup

The system-under-test, i.e. the execution runtime of the verifier, is regarded as a black box. It must expose an
arbitrary REST endpoint that accepts an `Authorization` header in which the test harness sends its self-issued ID token
as JWT. The system-under-test must evaluate that token according to the test specification listed below and return an
appropriate HTTP error code. 

## Verifying/Validating incoming SI Token requests

_For this test sequence the test harness aborts after it receives either an error response, or after it records an
outgoing VP query request from the verifier, respectively. The test harness will not respond to the VP query request._

| Number | Name                                        | Description                                                                                     | Sample data |
|--------|---------------------------------------------|-------------------------------------------------------------------------------------------------|-------------|
| R_0001 | Signature invalid                           | The signature of the JWT can not be verified using the public key from the counterparty.        |             |
| R_0002 | `sub_jwk` must not be present               | The JWT contains a `sub_jwk` claim                                                              |             |
| R_0003 | `jti` already used                          | The same `jti` claim value was used twice within the token's expiry time                        |             |
| R_0004 | `token` must be present | The JWT must contain a `token` claim, the shape of which is opaque          |             |
| R_0005 | `client_id` must be present                 | The JWT must contain a `client_id` claim, that must be identical to the sender's participant ID |             |

## Validating VerifiablePresentation responses

_This series of tests deals with validating a VP response returned from the CredentialService. Please note that basic
cryptographic integrity is asserted using the [VP/VC verification tests](./base_tests#verifying-ldp-vc--ldp-vp)
scheme, and basic schema validation is asserted using the [VP/VC validation](./base_tests#validating-ldp-vcs-and-vps).
This solely deals with IATP specific assertions._

| Number | Name                        | Description                                                                                                                                                                                  | Sample data |
|--------|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------|
| R_0006 | VC is not expired           | The `expirationDate` must be present and before the current time, possibly allowing for a reasonably small `epsilon`                                                                         |             |
| R_0007 | VC has correct subjects     | Every `credentialSubject` of every VC in the VP has an `id` property, which is equal to the prover's participant ID. This can be evaluated by the `client_id` claim of the original SI token |             |
| R_0008 | VC is not revoked           | The verifier rejects a VP that contains even one single revoked VC                                                                                                                           |             |
| R_0009 | VC has a valid issuer       | The `issuer` property of every VC is in the list of trusted issuers.                                                                                                                         |             |
| R_0010 | VP contains at least one VC | The verifier rejects empty VPs without a presentation.                                                                                                                                       |             |
