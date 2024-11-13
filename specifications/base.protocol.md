# Base Concepts

This section defines the core identity concepts used by the current specification.

## Decentralization

Decentralization is achieved in the following ways:

- **Self-Issued Identity Tokens** - Each participant is responsible for creating and cryptographically signing its own
  identity tokens. A central identity provider is therefore not required, eliminating a potential source of network
  failure.
- **Participant Verifiable Credential and Cryptographic Material Management** - Each participant is responsible for
  storing Verifiable Credentials and presenting them as Verifiable Presentations (VPs). Each participant is also
  responsible for storing and making its own cryptographic material available to Verifiers. This specification defines
  how VCs are presented and verified without the need for a third-party verification service.
- **Multiple Trust Anchors** - Participant VCs are signed by a third-party issuer acting as a trust anchor using a
  cryptographic proof. This specification allows for multiple trust anchors in a dataspace.

## Identities

The DSP specification mandates that all participants have a stable identifier (Participant ID). This specification
prescribes the Participant ID MUST be a DID as described in [[did-core]].

## Self-Issued ID Tokens

Participants use Self-Issued ID Tokens to authenticate themselves and present self-attested claims to a Verifier.
A Self-Issued ID Token is represented as a JSON Web Token [[rfc7519]] signed with a private key under the participant's
control.

The following claims MUST be included in the Self-Issued ID Token:

- The `iss` and `sub` claims MUST be equal and set to the bearer's (participant's) DID.
- The `aud` MUST be set to the Verifier's DID.
- The `jti` claim MUST be used to mitigate against replay attacks.
- The `exp` claim MUST be used to express the expiration time on or after which the ID Token MUST NOT be accepted by the
  verifier. Implementers MAY provide for some leeway to account for clock skew. Its value is a JSON [rfc8259] number
  representing the number of seconds from 1970-01-01T00:00:00Z as measured in UTC until the date/time.
- The `iat` claim MUST be used to specify the time the JWT was issued. Its value is a JSON number representing the
  number of seconds from 1970-01-01T00:00:00Z as measured in UTC.

### VP Access Token

A Self-Issued ID Token MAY contain an access token as a `token` claim that can be used by the Verifier to
obtain VPs from the participant's [=Credential Service=]. The format of the `token` is implementation-specific and
therefore should be treated as an opaque string by the Verifier.

### Obtaining Self-Issued ID Tokens

_This section is non-normative._

How a Self-Issued ID token is obtained from a [=Secure Token Service=] is implementation-specific. An STS may support a
variety of APIs, including OAuth 2.

#### Using the OAuth 2 Client Credential Grant

A Self-Issued ID Token may be obtained by executing a Client Credential Grant as defined in [[[?rfc6749]]]
against a [=Secure Token Service=] endpoint. How the participant agent obtains the STS endpoint address is
implementation-specific and beyond the scope of this specification.

If an `token` is required to be included in the Self Issued ID token, the participant agent will need to request
scopes for the access token. This is dependent on the API used to obtain the Self-Issued ID Token. An STS implementation
may use endpoint parameters as defined in the [[[?rfc6749]]] to convey metadata necessary for the creation of the
`token`
claim. In this case, the Self-Issued ID Token request may contain a `bearer_access_scope` authorization request
parameter set to a list of space-delimited scopes the `token` claim will be enabled for. If no `bearer_access_scope`
parameter is present, the `token` claim should not be included.

<aside class="note">
A non-normative OpenAPI spec of an STS implementing client credentials flow is provided [here](identity-trust-sts-api.yaml)
</aside>

### Validating Self-Issued ID Tokens

The Verifier MUST validate the Self-Issued ID Token using the following steps:

1. The Verifier MUST assert that the `iss` and `sub` claims have the same DID value.
2. The Verifier MUST assert that the `aud` claim is set to the Verifier's DID.
3. The Verifier MUST validate the signature of the Self-Issued ID token by using a key obtained from the resolved `sub`
   DID Document. DID resolution is performed according to the DID Method specified by the `sub` claim. The resolved DID
   document is processed to retrieve the value of the `verificationMethod` property. If no `kid` token header is
   specified and the `verificationMethod` property contains one entry, the verification method is used. If a `kid` token
   header is specified, the verification material from the entry corresponding the `kid` token header is used. If no
   matching entry is found, the token is rejected.
4. The Verifier MUST assert that the `sub` claim value equals the `id` property in the DID Document.
5. The Verifier MUST assert the `nbf` claim if present. The Verifier MAY allow for some leeway to account for clock
   skew.
5. The Verifier MUST assert that the current time is before the time represented by the `exp` claim. The Verifier MAY
   allow for some leeway to account for clock skew. The `iat` claim MAY be used to reject a token that was issued much
   earlier than the current time, thereby limiting the amount of time a `jti` value needs to be stored.
6. The Verifier MUST assert that the `jti` claim value has not been used previously.

# Schemas, Contexts, and Message Processing

All protocol messages are normatively defined by a [[json-schema]]. This specification also uses [[[json-ld11]]] and
provides a Json-Ld context to serialize data structures and message types as it facilitates extensibility. The Json-Ld
context is designed to produce message serializations using compaction that validate against the Json Schema for the
given message type. This allows implementations to choose whether to process messages as plain Json or as Json-Ld and
maintain interoperability between those approaches. Extensions that use Json-Ld are encouraged to provide similar
contexts that facilitate this approach to interoperability.

## The Decentralized Claims Protocol Context

The [[[json-ld11]]] context URI for the specification is: `https://w3id.org/dspace-dcp/v[version]`. The `version`
indicates a [Semantic Versioning](https://semver.org/) `MAJOR.MINOR` number. The current specifications use `0.8`
version and the following context URI: `https://w3id.org/dspace-dcp/v0.8`.
