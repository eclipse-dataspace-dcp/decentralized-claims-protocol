# Credential Issuance Protocol

The Credential Issuance Protocol defines the endpoints and message types for requesting [=Verifiable Credentials=] from
a [=Credential Issuer=]. The protocol is designed to handle use cases where credentials can automatically be issued and
where a manual workflow is required.

## Issuance Flow

The following sequence diagram depicts a non-normative flow where a client interacts with a [=Credential Issuer=] to
issue a [=Verifiable Credential=]:

![Issuance Flow](specifications/issuance.flow.svg "Issuance Flow")

1. The client sends a request to its [=Secure Token Service=] for a token including an access token. This could be a
   [=Self-Issued ID Token=]. The API used to make this request is implementation specific. The client may include a set
   of scopes that define the [=Verifiable Credentials=] the client wants the [=Issuer Service=] to provide. This set of
   scopes is determined out of band and may be derived from metadata the [=Credential Issuer=] has previously made
   available to the client.
2. The [=Secure Token Service=] responds with an access token a that may be in `token` claim a [=Self-Issued ID Token=].
   The access token can be used by the [=Issuer Service=] to write requested [=Verifiable Credentials=]
   to the client's [=Credential Service=].
3. The client makes a request to the [=Issuer Service=] for one or more [=Verifiable Credentials=] and includes
   a [=Self-Issued ID Token=] containing the access token.
4. The [=Issuer Service=] resolves the client [=DID=] based on the value of the [=Self-Issued ID Token=] `sub` claim.
5. The [=DID Service=] returns the DID Document. The [=Issuer Service=] validates the [=Self-Issued ID Token=] following
   Section [[[#validating-self-issued-id-tokens]]].
6. The [=Issuer Service=] rejects the request or acknowledges receipt.
7. At a later point in time, if the [=Verifiable Credential=] request is approved, the [=Issuer Service=] uses the
   resolved DID Document to obtain the client's [=Credential Service=] endpoint as described in
   Section [[[#credential-service-endpoint-discovery]]] and sends the [=Verifiable Credentials=] to
   the [=Credential Service=]. The send operation is performed asynchronously from the client request.
8. The [=Credential Service=] validates the access token and stores the [=Verifiable Credentials=].

## Issuer Service Endpoint Discovery

The client [=DID Service=] MUST make the [=Issuer Service=] available as a `service` entry ([[[did-core]]], sect.
5.4) in the DID document that is resolved by its DID. The `type` attribute of the `service` entry MUST be
`IssuerService`.

The `serviceEndpoint` property MUST be interpreted by the client as the base URL of the [=Issuer Service=]. The
following is a non-normative example of a `Issuer Service` entry:

<aside class="example" title="Credential Service Entry in DID document">
    <pre class="json">
{
  "@context": [
      "https://www.w3.org/ns/did/v1",
      "https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"
    ],
  "service": [
    {
      "id":"did:example:123#issuer-service",
      "type": "IssuerService", 
      "serviceEndpoint": "https://issuer.example.com"
    }
  ]
}
    </pre>
</aside>

## Issuer Service Base URL

All endpoint addresses are defined relative to the base URL of the [=Issuer Service=]. The [=Credential Issuer=] will
use the base URL for the `issuer` field in all [=Verifiable Credentials=] it issues as defined by the `issuer`
property ([[vc-data-model]]).

No assumptions are made about the base URL, for example, if it is a domain, subdomain, or contains a path.

## Credential Request API

The Credential Request API defines the REQUIRED [=Issuer Service=] endpoint for requesting [=Verifiable Credentials=].

The request MUST include an ID Token in the HTTP `Authorization` header prefixed with `Bearer` as defined in
the [[[#verifiable-presentation-access-token]]]. The `issuer` claim can be used by the [=Credential Service=] to resolve
the client's [=DID=] to obtain cryptographic material for validation and credential binding.

The ID Token MUST contain a `token` claim that is a bearer token granting write privileges for the
requested [=Verifiable Credentials=] into the client's `Credential Service` as defined
by [[[#verifiable-presentation-protocol]]]

The bearer token MAY also be used by the [=Issuer Service=] to resolve [=Verifiable Presentations=] the client is
required to hold for issuance of the requested [=Verifiable Credentials=].

If the issuer supports a pre-authorization code flow, the client MUST use the `pre-authorized_code` claim in the
Self-Issued ID Token to provide the pre-authorization code to the issuer.

|                 |                                                           |
|-----------------|-----------------------------------------------------------|
| **Sent by**     | Client that is a potential [=Holder=]                     |
| **HTTP Method** | `POST`                                                    |
| **URL Path**    | `/credentials`                                            |
| **Request**     | [`CredentialRequestMessage`](#credential-request-message) |
| **Response**    | `HTTP 201` OR `HTTP 4xx Client Error`                     |                                                    

### Credential Request Message

|              |                                                                                                                                                                                                                                                                                                                                                                              |
|--------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Schema**   | [JSON Schema](./resources/issuance/credential-request-message-schema.json)                                                                                                                                                                                                                                                                                                   |
| **Required** | - `@context`: Specifies a valid Json-Ld context ([[json-ld11]], sect. 3.1).                                                                                                                                                                                                                                                                                                  |
|              | - `type`: A string specifying the `CredentialRequestMessage` type                                                                                                                                                                                                                                                                                                            |
|              | - `holderPid`: A string corresponding to the request id on the Holder side type                                                                                                                                                                                                                                                                                              |
|              | - `credentials`: a JSON array of objects, each containing a `format`, which is A JSON string <br/>that describes the format of the credential to be issued <br/>, a `type`: A JSON array of strings that specifies the VC type being requested and <br/>optionally a `credentialSubject` property with client-supplied data that the client requests the [=Issuer=] to sign. |

The following is a non-normative example of a `CredentialRequestMessage`:

<aside class="example" title="CredentialRequestMessage">
    <pre class="json" data-include="./resources/issuance/example/credential-request-message.json">
    </pre>
</aside> 

The `credentials.format` string SHOULD clearly identify the data format and proof mechanism of the credential, for
example `"vcdm11_jwt"` or `"vcdm20_jose"`. The set of allowed values is implementation-specific.

On successful receipt of the request, the [=Issuer Service=] MUST respond with a `201 CREATED` and the `Location`
header set to the location of the request status ([[[#credential-request-status-api]]])

The [=Issuer Service=] MUST respond with `401 Not Authorized` if the request is unauthorized.

The [=Issuer Service=] MUST respond with `400 Bad Request` if the `credentials` array includes an object with a
`credentialSubject` property that either violates or extends the corresponding `credentialSchema` of
the [[[#credentialobject]]].

The [=Issuer Service=] MUST respond with `501 Not Implemented` and an appropriate response body if it does not support
the optional client-supplied `credentialSubject` property at all or for a specific `credentialType`.

The [=Issuer Service=] MAY respond with `400 Bad Request` and an appropriate response body if it refuses to issue a
Credential according to the client-supplied data in the `credentialSubject` property.

If the request is approved, the issuer endpoint will send an acknowledgement to the client. The [=Issuer Service=] MAY
still reject a request after initial acknowledgement and signify this via the [[[#credential-request-status-api]]].
the [=Verifiable Credentials=] are ready, the [=Issuer Service=] will respond asynchronously with a write-request to the
client's `Credential Service` using the Storage API defined in Section [[[#storage-api]]]. 

## Storage API

The Storage API defines the REQUIRED [=Credential Service=] endpoint for writing issued credentials, typically invoked
by
an [=Issuer Service=].

If a client is not authorized for an endpoint request, the [=Credential Service=] SHOULD return `4xx Client Error`. The
exact error code is implementation-specific.

|                 |                                           |
|-----------------|-------------------------------------------|
| **Sent by**     | [=Issuer Service=]                        |
| **HTTP Method** | `POST`                                    |
| **URL Path**    | `/credentials`                            |
| **Request**     | [Credential Message](#credential-message) |
| **Response**    | `HTTP 2xx` OR `HTTP 4xx Client Error`     |

### Credential Message

|              |                                                                                                                      |
|--------------|----------------------------------------------------------------------------------------------------------------------|
| **Schema**   | [JSON Schema](./resources/issuance/credential-message-schema.json)                                                   |
| **Required** | - `@context`: Specifies a valid Json-Ld context ([[json-ld11]], sect. 3.1)                                           |
|              | - `type`: A string specifying the `Credential Message` type.                                                         |
|              | - `issuerPid`: A string corresponding to the issuance id on the Issuer side.                                         |
|              | - `holderPid`: A string corresponding to the issuance id on the Holder side.                                         |
|              | - `status`: A string stating whether the request was successful (`ISSUED`) or rejected (`REJECTED`)                  |
|              | - `credentials`: An array of [Credential Container](#credential-container) Json objects as defined in the following. |
|              | - `rejectionReason`: a String containing additional information why a request was rejected. Can be `null`.           |

The following is a non-normative example of a [Credential Message](#credential-message) JSON body:

<aside class="example" title="Credential Message (issued)">
    <pre class="json" data-include="./resources/issuance/example/credential-message.json">
    </pre>
</aside>

The following example shows a rejected credential request JSON body:
<aside class="example" title="Credential Message (rejected)">
    <pre class="json" data-include="./resources/issuance/example/credential-message-rejected.json">
    </pre>
</aside>

Note that the `status` applies to the entire request, i.e. a request is considered _rejected_ if at least one credential
could not be issued.
Allowed values for the `status` are `"ISSUED"` and `"REJECTED"`. The `rejectionReason` field is optional and should not
disclose any confidential information.

### Credential Container

The [Credential Message](#credential-message)'s `credentials` property contains an array of `CredentialContainer`
objects.
The  [Credential Container](#credential-container) object contains the following properties:

|              |                                                                                                                                                                                                                            |
|--------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Schema**   | [JSON Schema](./resources/issuance/credential-message-schema.json)                                                                                                                                                         |
| **Required** | - `credentialType`: A single string specifying type of credential. See [VC DataModel 1.1](https://www.w3.org/TR/vc-data-model/#types) or [VC DataModel 2.0](https://www.w3.org/TR/vc-data-model-2.0/#types), respectively. |
|              | - `payload`: A Json Literal ([[json-ld11]], sect. 4.2.2) containing a [=Verifiable Credential=] defined by ([[vc-data-model]]).                                                                                            |
|              | - `format`:  a JSON string that describes the format of the credential to be issued                                                                                                                                        |

## Credential Offer API

Some scenarios involve the [=Credential Issuer=] making an initial offer. For example, an out-of-band process may result
in
a credential offer. Or, a [=Credential Issuer=] may start a key rotation process which requires
a [=Verifiable Credential=] to be
reissued. In this case, the [=Credential Issuer=] can proactively prompt a [=Holder=] to request a
new [=Verifiable Credential=]
during the key rotation period.

The Credential Offer API defines the REQUIRED [=Credential Service=] endpoint for notifying a [=Holder=] of
a [=Verifiable Credential=] offer.

|                 |                                                       |
|-----------------|-------------------------------------------------------|
| **Sent by**     | [=Credential Issuer=]                                 |
| **HTTP Method** | `POST`                                                |
| **URL Path**    | `/offers`                                             |
| **Request**     | [`CredentialOfferMessage`](#credential-offer-message) |
| **Response**    | `HTTP 200` OR `HTTP 4xx Client Error`                 |                                                    

### Credential Offer Message

|              |                                                                                                                    |
|--------------|--------------------------------------------------------------------------------------------------------------------|
| **Schema**   | [JSON Schema](./resources/issuance/credential-offer-message-schema.json)                                           |
| **Required** | - `@context`: Specifies a valid Json-Ld context ([[json-ld11]], sect. 3.1)                                         |
|              | - `type`: A string specifying the `CredentialOfferMessage` type                                                    |
|              | - `issuer`:  The [=Credential Issuer=] DID                                                                         |
|              | - `credentials`: A JSON array, where every entry is a JSON object of type [[[#credentialobject]]] or a JSON string |

If the `credentials` property entries are type string, the value MUST be one of the `id` values of an object in the
`credentialsSupported` returned from the [[[#issuer-metadata-api]]]. When processing, the [=Credential Service=]
MUST resolve this string value to the respective object.

The following is a non-normative example of a credential offer request:

<aside class="example" title="CredentialOfferMessage">
    <pre class="json" data-include="./resources/issuance/example/credential-offer-message.json">
    </pre>
</aside>   

### CredentialObject

|              |                                                                                                                                                                                                                               |
|--------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Schema**   | [JSON Schema](./resources/issuance/credential-object-schema.json)                                                                                                                                                             |
| **Required** | - `type`: A string specifying the `CredentialObject` type                                                                                                                                                                     |
|              | - `credentialType`: A single string specifying type of credential being offered                                                                                                                                               |
| **Optional** | - `@context`: Specifies a valid Json-Ld context ([[json-ld11]], sect. 3.1). As the `credentialObject` is usually embedded, its context is provided by the enveloping object.                                                  |
|              | - `bindingMethods`: An array of strings defining the key material that an issued credential is bound to                                                                                                                       |
|              | - `credentialSchema`: A URL pointing to the credential schema of the object in a VC's `credentialSubject` property.                                                                                                           |
|              | - `profiles`: An array of strings containing the aliases of the [profiles](#profiles-of-the-decentralized-claims-protocol), e.g. `"vc20-bssl/jwt"`                                                                            |
|              | - `issuancePolicy`: A [presentation definition](https://identity.foundation/presentation-exchange/spec/v2.0.0/#presentation-definition) [[presentation-ex]] signifying the required [=Verifiable Presentation=] for issuance. |
|              | - `offerReason`: A reason for the offer as a string. Valid values may include `reissue` and `proof-key-revocation`                                                                                                            |

The following is a non-normative example of a `CredentialObject`:

<aside class="example" title="CredentialObject">
    <pre class="json" data-include="./resources/issuance/example/credential-object.json">
    </pre>
</aside>  

## Issuer Metadata API

The Issuer Metadata API defines the REQUIRED [=Issuer Service=] endpoint for conveying verifiable credential types
supported by the [=Credential Issuer=].

|                 |                                  |
|-----------------|----------------------------------|
| **Sent by**     | A client                         |
| **HTTP Method** | `GET`                            |
| **URL Path**    | `/metadata`                      |
| **Response**    | [[[#issuermetadata]]] `HTTP 200` |

### IssuerMetadata

|              |                                                                             |
|--------------|-----------------------------------------------------------------------------|
| **Schema**   | [JSON Schema](./resources/issuance/issuer-metadata-schema.json)             |
| **Required** | - `@context`: Specifies a valid Json-Ld context ([[json-ld11]], sect. 3.1). |
|              | - `type`: A string specifying the `IssuerMetadata` type                     |
|              | - `issuer`: A string containing the [=Credential Issuer=] DID               |
| **Optional** | - `credentialsSupported`: A JSON array of [[[#credentialobject]]] elements  |

The following is a non-normative example of a `IssuerMetadata` response object:

<aside class="example" title="IssuerMetadata">
    <pre class="json" data-include="./resources/issuance/example/issuer-metadata.json">
    </pre>
</aside>  

## Credential Request Status API

The Credential Request Status API defines the REQUIRED [=Issuer Service=] endpoint for conveying the status of a
[=Verifiable Credential=] request.

|                 |                                                                                                                                                         |
|-----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Sent by**     | A client                                                                                                                                                |
| **HTTP Method** | `GET`                                                                                                                                                   |
| **URL Path**    | `/requests/<request id>`  where the request id corresponds to the ID identified by the location header returned for a [[[#credential-request-message]]] |
| **Response**    | [[[#credentialstatus]]] `HTTP 200`                                                                                                                      |     

The [=Issuer Service=] MUST implement access control such that only the client that made the request may access a
particular request status. A [=Self-Issued ID Token=] MUST be submitted in the HTTP `Authorization` header prefixed
with `Bearer` of the request.

### CredentialStatus

|              |                                                                             |
|--------------|-----------------------------------------------------------------------------|
| **Schema**   | [JSON Schema](./resources/issuance/credential-status-schema.json)           |
| **Required** | - `@context`: Specifies a valid Json-Ld context ([[json-ld11]], sect. 3.1). |
|              | - `type`: A string specifying the `CredentialStatus` type                   |
|              | - `issuerPid`: A string corresponding to the issuance id on the Issuer side |
|              | - `holderPid`: A string corresponding to the issuance id on the Holder side |
|              | - `status`: A string with a value of `RECEIVED`, `REJECTED`, or `ISSUED`    |

The following is a non-normative example of a `CredentialStatus` response object:

<aside class="example" title="CredentialStatus">
    <pre class="json" data-include="./resources/issuance/example/credential-status.json">
    </pre>
</aside>  

## Key Rotation and Revocation

[=Issuer Service=] implementations SHOULD support rotation and revocation of keys used to
create [=Verifiable Credential=] proofs.

### Key rotation

Key rotation may be supported in the following way:

1. After a defined `cryptoperiod`, a rotation is initiated, a new key pair is generated and the public key is added
   to a `verificationMethod` in the [=Credential Issuer=] DID document. The new private key is used to sign newly
   issued [=Verifiable Credential=] proofs.
2. The old private key is decommissioned (archived or destroyed). However, the `verificationMethod` in
   the [=Credential Issuer=] DID document are retained so existing issued [=Verifiable Credentials=] may be verified.
3. At some point before existing [=Verifiable Credentials=] are set to expire, an [=Credential Issuer=] may make
   credential offers for new [=Verifiable Credentials=] to [=Holders=] with the `offerReason = reissue` property.
4. After a defined period, the rotated key's `verificationMethod` will be removed from the [=Credential Issuer=] DID
   document. This period must at least extend to the expiration date of the last issued credential. At this point, any
   existing [=Verifiable Credentials=] with proofs signed by the old (now rotated) key will become invalid.

Implementors following this sequence SHOULD set the `expirationDate`/`validUntil` property of
issued [=Verifiable Credentials=] to less than the rotation period of the keys used to sign their proofs. Rotated keys
SHOULD not be decommissioned until all credentials, that were signed with it, have expired.

### Key revocation

Key revocation may be supported in the following way:

1. After revocation is initiated, a new key pair is generated and the public key is added to a `verificationMethod` in
   the [=Credential Issuer=] DID document. The new private key is used to sign newly issued [=Verifiable Credential=]
   proofs.
2. The old private key is decommissioned (archived or destroyed) and the `verificationMethod` in
   the [=Credential Issuer=] DID document is removed.
3. The [=Credential Issuer=] may make credential offers for new [=Verifiable Credentials=] to [=Holders=] with the
   `offerReason = proof-key-revocation` property.

Upon revocation of a key pair, all credentials that were issued and proofed with that key, immediately become invalid.

## Verifiable Credential Revocation

[=Verifiable Credential=] revocation MUST be supported using the [[[vc-bitstring-status-list-20230427]]] specification.
Note that implementations MAY support multiple lists.
