# Verifying SI Tokens in the CredentialService

In a typical scenario involving IATP, there is one party that wants to prove certain claims about themselves, and there
is one party that wants to verify those claims. Topologically the CredentialService belongs to the "proving" party.

This document deals with tests for the CredentialService.

## Test setup

The system-under-test, i.e. the execution runtime of the CredentialService, is regarded as a black box, exposing only
the
Resolution API. The test harness then executes REST requests against that API and performs test assertions. It may be
necessary for the test harness to interact with the system-under-test, e.g. to obtain valid `presentation_access_tokens`
or to prepare a specific (set of) VCs.

## Verifying/Validating incoming SI Token requests

_This test sequence handles only negative cases, where self-issued ID tokens (created by a supposed verifier) are
rejected by the CredentialService. Consequently, the test harness stops the test once a rejection response was received,
or a reasonable time has passed. Cases, where the correctness and validity of the generated VPs is asserted are handled
below._

| Number | Name                                        | Description                                                                                                                                                               | 
|--------|---------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| P_0001 | Request body invalid                        | The request body of the `/presentation/query` endpoint is invalid, e.g. it contains both a `scope` _and_ a `presentationDefinition` object, or it cannot be deserialized. |
| P_0002 | No `Authorization` header present           | The request does not have an `Authorization` header (i.e. no token is present)                                                                                            |
| P_0003 | Signature invalid                           | The signature of the JWT can not be verified using the public key from the counterparty.                                                                                  |
| P_0004 | `sub_jwk` must not be present               | The JWT contains a `sub_jwk` claim                                                                                                                                        |
| P_0005 | `jti` already used                          | The same `jti` claim value was used twice within the token's expiry time                                                                                                  |
| P_0006 | `presentation_access_token` must be present | The JWT must contain a `presentation_access_token` claim, the shape of which is opaque                                                                                    |
| P_0007 | `client_id` must be present                 | The JWT must contain a `client_id` claim, that must be identical to the sender's participant ID                                                                           |
| P_0008 | Issuer's scope not valid                    | The `presentation_access_token` cannot be resolved to a valid scope. That means, the scope string that is encoded in the token is invalid or is not understood.           |
| P_0009 | Requestor's query not valid                 | The `scope` or `presentationDefinition` object of the presentation query cannot be parsed to valid scope string.                                                          |
| P_0010 | Requestor's query is unauthorized           | The requestor is not authorized for at least one item specified by the query. In practice, this could be a scope string that is "too wide"                                |

## Verifying/Validating the created VP

_This test sequence deals with ensuring, that all VPs created by the CredentialService are structurally and
cryptographically intact. For basic cryptographic checks please also refer to
the [VC/VP verification section](./base_tests#verifying-ldp-vc--ldp-vp)_

| Number | Name                                 | Description                                                                                                                                                                                                                    |
|--------|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| P_0011 | LDP-VP contains single LDP-VC        | The LDP-VP contains a single LDP-VC.                                                                                                                                                                                           |
| P_0012 | LDP-VP contains multiple LDP-VC      | The LDP-VP contains an array of LDP-VCs                                                                                                                                                                                        |
| P_0013 | JWT-VP contains single JWT-VC        | A JWT-VP contains a single JWT-VC.                                                                                                                                                                                             |
| P_0014 | JWT-VP contains multiple mixed VC    | A JWT-VP contains a mixtures of JWT-VCs and LDP-VCs (at least one each).                                                                                                                                                       |
| P_0015 | JWT-VCs are wrapped in a separate VP | An LDP-VP cannot contain JWT-VCs, they would get stripped out during JSON-LD expansion. Therefor if the desired format is `ldp-vp`, the CredentialService must return a second VP, formatted as JWT, that contains all JWT-VCs |