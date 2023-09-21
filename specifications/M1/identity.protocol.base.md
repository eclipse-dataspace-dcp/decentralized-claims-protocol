# 1. Introduction

This document defines a base protocol for communicating participant identities and claims in a Tractus-X dataspace. This
specification assumes familiarity with the [Tractus-X Dataspace Topology Specification](tx.dataspace.topology.md).

# 2. Motivation

The key goal of this protocol specification is to minimize the risk of business disruption related to the failure of
identity and credential systems in a Tractus-X dataspace. As such, it provides a design for a decentralized system to
communicate participant identities and [Verifiable Credentials](https://www.w3.org/TR/vc-data-model/) (VCs).

> Note that it is not a design goal of this protocol to support a self-sovereign data exchange network where each
> participant is in full control of its ability to operate on the network. Specifically, participants do not perform
> self-registration, and instead rely on a _**Registration Service**_. Imposing this restriction provides a mechanism
> for business security (the field of participants can be restricted through a verification process) and greatly
> simplifies the technical problem at hand.

## 2.1. Decentralization

Decentralization is achieved in the following ways:

- **Self-Issued Identity Tokens** - Each participant is responsible for creating and cryptographically signing its own
  identity tokens. A central identity provider is therefore not required, eliminating a potential locus of network
  failure.
- **Participant VC and Cryptographic Material Management** - Each participant is responsible for storing VCs and
  presenting them (VPs) in the network. Each participant is also responsible for storing and making its own
  cryptographic material available to verifiers in the network. This specification defines how VCs are presented and
  verified without the need for a third-party verification service.
- **Multiple Trust Anchors** - Participant VCs are signed by a third-party issuer acting as a trust anchor using a
  cryptographic proof. The identity protocol allows for multiple trust anchors in a dataspace.

# 3. Identities and Identifiers

Each participant MUST have a unique, immutable **_identity_** provided by the `Registration Service` and
a [Web DID](https://w3c-ccg.github.io/did-method-web/) that it chooses. This relationship is expressed as:

```
ID  ------ Can resolve to -----> DID
 ^                                |
 |                                |
 |----------Associated with--------                               
```

This immutable identity is termed the `participant id`.

## 3.1.The Membership VC

Dataspaces which implement the TX identity protocol MUST define a VC that adheres to
the [Verifiable Credentials Data Model v1.1](https://www.w3.org/TR/vc-data-model/) and cryptographically binds the
`participant id` to its `Web DID`. This VC is termed the `Membership VC`. The VC verifier's cryptographic material MUST
be resolvable as a `Web DID`.

> TODO: do we want to define the subject material of the Membership VC or should we leave it dataspace specific?

# 4. Self-Issued ID Tokens

A Self-Issued ID Token is defined in
the [Self-Issued OpenID Provider v2 specification](https://openid.net/specs/openid-connect-self-issued-v2-1_0.html#section-1.1) :

> In the Self-Issued OP case, the ID Token is self-signed with a private key under the user's control, identified by the
> sub-claim.

A client may obtain a Self-Issued ID Token using a variety or OAuth grant types. If the OAuth 2.0 Client Credential Grant
type is used, the client MUST conform
to [Section 6](#6-using-the-oauth-2-client-credential-grant-to-obtain-access-tokens-from-an-sts).

# 4.1. Self-Issued ID Token Contents

The Self-Issued ID Token MUST adhere
to [JSON Web Token (JWT) Profile for OAuth 2.0 Access Tokens](https://datatracker.ietf.org/doc/html/rfc9068) and MUST
include the following claims:

- The `iss` and `sub` claims MUST be equal and set to the bearer's (participant's) web DID.
- The `sub_jwk` claim is not used
- The `aud` set to the `participant_id` of the relying party (RP)
- The `client_id` set to the `participant_id` of the consumer
- The `jti` claim that is used to mitigate against replay attacks
- All VPs in the `vp` claim MUST be in the format specified by
  the [Verifiable Credentials Data Model v1.1](https://www.w3.org/TR/vc-data-model/)

> Note implementations should consider HTTP header restriction sizes when including a `vp` claim. If the claim size
> exceeds 3-4K, a separate protocol should be used that allows the RP to resolve VPs out-of-band.

## 4.1.1. VP Access Token

A Self-Issued ID Token MAY contain an access token as an `access_token` claim that can be used by the relying party to
obtain additional VPs.

> TODO: determine claim name

# 4.2. Validating Self-Issued ID Tokens

The relying party MUST follow the steps specified in
the [Self-Issued OpenID Provider v2 specification](https://openid.net/specs/openid-connect-self-issued-v2-1_0.html#section-11.1).

# 5. Verifiable Presentations

Additional client metadata such as Verifiable Presentations can be obtained by a relying party (RP) using the
client's `DID`, typically specified in the `sub` claim of a Self-Issued ID Token. The DID document may contain `service`
entries that can be used to resolve metadata.

# 6. Using the OAuth 2 Client Credential Grant to Obtain Access Tokens from an STS

A Self-Issued ID Token MAY be obtained by a participant agent executing
an [OAuth 2.0 Client Credential Grant](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.4) against a Secure Token
Service (STS) Endpoint. How the participant agent obtains the endpoint address is participant-specific and beyond the
scope of this specification.

The Self-Issued ID Token request MAY contain the `bearer_access_scope` authorization request parameter which is set to a
list of space-delimited scopes the response `VP Access Token` set in the `access_token` claim will be enabled for. If
no `bearer_access_scope` parameter is present, the `access_token` claim MUST not be included.
