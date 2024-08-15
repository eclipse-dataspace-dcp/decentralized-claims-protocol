# 1. Introduction

This specification defines a protocol for Verifiable Credential (VC) issuance. Specifically, the Credential Issuance
Protocol (CIP) defines the endpoints and message types for requesting credentials to be issued from
a `Credential Issuer.`

This specification relies on the [Base Identity Protocol](identity.protocol.base.md) and
the [Verifiable Presentation Protocol](verifiable.presentation.protocol.md).

## 1.1. Motivation

Verifiable Credentials enable a holder to present claims directly to a Relying Party (RP) without
the involvement or knowledge of the `Credential Issuer`. The Credential Issuance Protocol (CIP) provides an
interoperable mechanism for parties (potential holders) to request credentials from a `Credential Issuer.` The protocol
is designed to handle use cases where credentials can automatically be issued and where a manual workflow is required.

This specification draws from the
[OpenID for Verifiable Credential Issuance specification](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#name-introduction)
but differs in its focus on service-to-service interactions where end-user devices are not involved in the
protocol flow. Moreover, the current specification accommodates the requirement for long-running interactions typically
associated with manual workflows that are best modelled using asynchronous messaging paradigms.

## 1.2 Terms

- ***DID*** - A decentralized identifier as defined by the [DID specification](https://github.com/w3c/did-core).

# 2. Overview

The Credential Issuance Protocol is designed to be used in conjunction with
the [Base Identity Protocol](identity.protocol.base.md) and the
[Verifiable Presentation Protocol](verifiable.presentation.protocol.md). This issuance interaction flow is expressed
in the following diagram:

![Issuance Flow](issuance.flow.png)

In the above sequence, the client uses the `Base Identity Protocol` to create a Self-Issued Identity token that it
includes in its request to the `Credential Issuer.` If the VC request is approved by the `Credential Issuer`, a VC will
be written to the client's `Credential Service` using the `Verifiable Presentation Protocol.` The operation is performed
asynchronously from the client request, resulting in non-blocking behavior.

## 2.1. The Issuer Base URL

All endpoint addresses are defined relative to the base URL of the issuer service. The base URL MUST use the HTTPS
scheme. The issuer will use the base URL for the `issuer` field in all VCs it issues as defined by
the [issuer property](https://www.w3.org/TR/vc-data-model/#dfn-property).

This specification makes no assumption about the base URL, for example, if it is a domain, subdomain, or contains a
path.

# 3. Credential Request Flow

The `credential request flow` is initiated by a client making a request for one or more VCs to an
issuer's `Credential Request Endpoint`. If the request is valid, the issuer endpoint will send an acknowledgement to the
client. If the request is approved, the VC will be issued to the client asynchronously.

## 3.1. Credential Request Endpoint

Communication with the `Credential Request Endpoint` MUST utilize TLS.

The credential request endpoint MUST be available under the `POST` method at `/credentials` relative to the base URL of
the
issuer.

The request MUST include an ID Token in the HTTP `Authorization` header prefixed with `Bearer` as defined in
the [Base Identity Protocol Specification](identity.protocol.base.md#411-vp-access-token). The `issuer` claim can be
used by the Credential Issuer to resolve the client's DID to obtain cryptographic material for validation and credential
binding.

The ID Token MUST contain a `token` claim that is a bearer token granting write privileges for the
requested VCs into the client's `Credential Service` as defined by
the [Verifiable Presentation Protocol specification](verifiable.presentation.protocol.md)

The ID Token MAY contain an `token` claim as defined in
the [Base Identity Protocol Specification](identity.protocol.base.md)  claim that can be used by the issuer to resolve
Verifiable Presentations (VP) the client is required to hold for issuance of the requested VCs.

If the issuer supports a pre-authorization code flow, the client must use the `pre-authorized_code` claim in the ID
Token to provide the pre-authorization code to the issuer.

### 3.1.1. Credential Request Parameters

The Credential Request `POST` body MUST be a `CredentialRequestMessage` JSON object with the following properties:

- `@context`: REQUIRED. Specifies a valid [Json-Ld context](https://www.w3.org/TR/json-ld11/#the-context).
- `@type`: REQUIRED. A string specifying the `CredentialRequestMessage` type.
- `format`: REQUIRED. A JSON string that describes the format of the credential to be issued. Implementations MUST
  support the `ldp_vc` format as defined by
  the [OpenID for Verifiable Credential Issuance specification](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#section-e.1.3).
  Implementations MAY support other VC formats.
- `type`: REQUIRED. A JSON array of strings that specifies the VC type being requested.

The following is a non-normative example of a `CredentialRequestMessage`:

```
POST /credentials HTTP/1.1
Host: server.example.com
Content-Type: application/json
Authorization: Bearer ......

{
   "@context": [
    "https://w3id.org/tractusx-trust/v0.8",
     "https://www.w3.org/2018/credentials/v1"
   ],
   "@type": "CredentialRequestMessage",
   "format":"ldp_vd",
   "type":[
      "VerifiableCredential",
      "EntityCredential"
   ],
}
```

On successful receipt of the request, the Credential Issuer MUST respond with a `201 CREATED` with the `Location`
header set to the location of the request status. See
the [Credential Request Status](#credential-request-status-endpoint) section.

The issuer MAY respond with `401 Not Authorized` if the request is unauthorized or other `HTTP` status codes to indicate
an exception.

If the VC request is approved, the issuer will respond with a write-request to the client's `Credential Service` using
the Storage API defined in the [Verifiable Presentation Protocol](verifiable.presentation.protocol.md#5-storage-api).

# 4. Credential Offer Flow

Some scenarios involve the Credential Issuer making an initial offer. For example, an out-of-band process may result in
a credential offer. Or, a Credential Issuer may start a key rotation process which involves sending updated credentials
to holders signed with the issuer's new key. In this case, the issuer can proactively prompt holders to request a new
credential during the key rotation period.

## 4.1. Credential Offer Endpoint

Communication with the `Credential Offer Endpoint` MUST utilize TLS.

The credential offer endpoint MUST be available under the `POST` method at `/offers` relative to the base URL of the
holder's `Credential Service` base URL. Issuers can obtain this URL by resolving the holder's DID and inspecting
its `CredentialService` service entry.

### 4.1.1. Credential Offer Parameters

The Credential Offer `POST` body MUST be a `CredentialOfferMessage` JSON object with the following properties:

- `@context`: REQUIRED. Specifies a valid [Json-Ld context](https://www.w3.org/TR/json-ld11/#the-context).
- `@type`: REQUIRED. A string specifying the `CredentialOfferMessage` type.
- `credentialIssuer`: REQUIRED. The identifier of the Credential Issuer, the `Credential Service` is requested to obtain
  one or more credentials from.
- `credentials`: REQUIRED. A JSON array, where every entry is a JSON object or a JSON string.
    - entry type object: data MUST adhere to the [Credentials Object](#412-the-credentialobject)
    - entry type string: value MUST be one of the id values in one of the objects in the `credentials_supported`.
    - When processing, the `Credential Service` MUST resolve this string value to the respective object.

The following is a non-normative example of a credential offer request:

```
POST /credentials HTTP/1.1
Host: server.example.com
Content-Type: application/json
Authorization: Bearer ......

{
   "@context": [
    "https://w3id.org/tractusx-trust/v0.8",
     "https://www.w3.org/2018/credentials/v1"
   ],
   "@type": "CredentialOfferMessage",
   "credentialIssuer" :"...",
   "credentials: [...]
   
}
```                        

#### 4.1.2. The `CredentialObject`

The `CredentialObject` defines the following properties:

- `@type`: REQUIRED. A string specifying the `CredentialObject` type.
- `format`: REQUIRED. The format of the credential to be requested as defined by
  the [OpenID for Verifiable Credential Issuance specification](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#format_profiles).
- `credentialType`: REQUIRED. An array defining the type of credential being offered.
- `bindingMethods`: OPTIONAL. Binding methods supported as defined by `cryptographic_binding_methods_supported` in the
  _Open ID for Verifiable Credential Issuance_ specification.
- `cryptographicSuites`: OPTIONAL. Binding methods supported as defined by `cryptographic_suites_supported` in the
  _Open ID for Verifiable Credential Issuance_ specification.
- `issuancePolicy`: OPTIONAL. An [ODRL Policy](https://www.w3.org/TR/odrl-model/). Note that the ODRL policy MUST not
  contain `target` attributes. Implementations MAY not support ODRL issuance policies.
- `offerReason`: OPTIONAL. A reason for the offer as a string. Valid values may include `reissue`
  and `proof-key-revocation`.

> Note: Properties mapped to the _Open ID for Verifiable Credential Issuance_ specification are defined in
>
the [Credential Issuer Metadata](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#section-10.2.3.1)
> section.

The following is a non-normative example of a `CredentialObject`:

```json
{
  "@context": {
    "iatp": "https://w3id.org/tractusx-trust/v0.8",
    "odrl": "https://www.w3.org/ns/odrl.jsonld"
  },
  "@type": "CredentialObject",
  "credentialType": [
    "VerifiableCredential",
    "CompanyCredential"
  ],
  "format": "ldp_vc",
  "offerReason": "reissue",
  "bindingMethods": [
    "did:web"
  ],
  "cryptographicSuites": [
    "JsonWebKey2020"
  ],
  "issuancePolicy": {
    "permission": [
      {
        "action": "use",
        "constraint": {
          "and": [
            {
              "leftOperand": "iatp:CredentialPrereq",
              "operator": "eq",
              "rightOperand": "active"
            }
          ]
        }
      }
    ]
  }
}
```

# 5. Issuer Metadata endpoint

A credential issuer MUST support the Issuer Metadata endpoint using the HTTPS scheme and the `GET method`. The URL of
the endpoint is the base issuer url with the appended path `/.well-known/vci`.

The response is a `IssuerMetadata` JSON object with the following properties:

- `@context`: REQUIRED. Specifies a valid [Json-Ld context](https://www.w3.org/TR/json-ld11/#the-context).
- `@type`: REQUIRED. A string specifying the `IssuerMetadata` type.
- `credentialIssuer`: REQUIRED. A unique identifier of the issuer, for example, a DID.
- `credentialsSupported`: OPTIONAL. A Json Array containing a list of `CredentialObject` JSON objects with properties
  corresponding to [Credential Objects](#412-the-credentialobject).

The following is a non-normative example of a `IssuerMetadata` response object:

```json
{
  "@context": {
    "iatp": "https://w3id.org/tractusx-trust/v0.8",
    "odrl": "https://www.w3.org/ns/odrl.jsonld"
  },
  "@type": "IssuerMetadata",
  "credentialIssuer": "did:web:issuer-url",
  "credentialsSupported": [
    {
      "credentialType": [
        "VerifiableCredential",
        "CompanyCredential"
      ],
      "offerReason": "reissue",
      "bindingMethods": [
        "did:web"
      ],
      "cryptographicSuites": [
        "JsonWebKey2020"
      ],
      "issuancePolicy": {
        "permission": [
          {
            "action": "use",
            "constraint": {
              "and": [
                {
                  "leftOperand": "iatp:CredentialPrereq",
                  "operator": "eq",
                  "rightOperand": "active"
                }
              ]
            }
          }
        ]
      }
    }
  ]
}
```

# 6. Credential Request Status Endpoint

The issuer MUST provide an `HTTPS GET` endpoint for retrieving the status of a credential at the base issuer url with
the appended path `/requests/<request id>`. The issuer SHOULD implement access control such that only the client that
made the request may access a particualr request status.

If accepted, a `CredentialStatus` Json object with a `status` property set to one of the following
values: `RECEIVED` | `REJECTED` | `ISSUED` will be returned.

The response is a `CredentialStatus` JSON object with the following properties:

- `@context`: REQUIRED. Specifies a valid [Json-Ld context](https://www.w3.org/TR/json-ld11/#the-context).
- `@type`: REQUIRED. A string specifying the `CredentialStatus` type.
- `requestId`: REQUIRED. A string corresponding to the request id 
- `status`: REQUIRED. A string equal to the one of the values: `RECEIVED`, `REJECTED`, or `ISSUED`.

The following is a non-normative example of a `CredentialStatus` response object:

```json
{
  "@context": {
    "iatp": "https://w3id.org/tractusx-trust/v0.8"
  },
  "@type": "CredentialStatus",
  "requestId": "...",
  "status": "RECEIVED"
}
```

# 7. Key Rotation and Revocation

Issuer implementations SHOULD support rotation and revocation of keys used to create VC proofs. Key rotation and
revocation may be supported in the following way:

1. After a defined `cryptoperiod`, a rotation is initiated, and a new key pair is generated and the public key is added
   to a `verficationMethod` in the issuer's DID document. The new private key is used to sign newly issued VC proofs.
2. The old private key is decommissioned (archived or destroyed). However, `verificationMethods` in the issuer's DID
   document are retained so existing issued VCs may be verified.
3. At some point before existing VCs are set to expire, an issuer may make credential offers for new VCs to holders.
4. After a defined period, revocation will be performed where the public key's `verificationMethods` will be removed
   from the issuer's DID document. At this point, any existing VCs with proofs signed by the revoked key will not
   verify.

Implementors following this sequence should set the `expirationDate` property of issued VCs to less than
the rotation period of the keys used to sign their proofs.

# 8. VC Revocation

VC revocation MUST be supported using the [Status List](https://www.w3.org/TR/vc-status-list/) specification. Note that
implementations MAY support multiple lists.

# 9. Issuer Endpoint resolution through DID Documents

Different methods may be used to resolve the base location of an issuer service. One way is
through DID documents. If a DID document is used, the client `DID document` MUST contain at least
one [service entry](https://www.w3.org/TR/did-core/#services) of type `IssuerService`:

```
{
  "service": [
    {
      "id":"did:example:123#issuer-service",
      "type": "IssuerService", 
      "serviceEndpoint": "https://issuer.example.com"
    }
  ]
}
```

The `serviceEndpoint` URL is the base URL for the Issuer Service.

> TODO: Add `IssuerService` namespace

10. ODRL (Open Digital Rights Language) Profile

An ODRL issuance and re-issuance policy may be associated with a set of `scopes` or
a [DIF Presentation Exchange presentation definition](https://identity.foundation/presentation-exchange/spec/v2.0.0/#presentation-definition).

This specification defines two ODRL attributes for the [Policy class](https://www.w3.org/TR/odrl-model/#policy) under
the `iatp` namespace:

- **scope** - Either a single `string` or an `array` of strings containing `scope` values
- **presentationDefinition** - Either an object containing an `@id` attribute with a URI value referencing
  a `presentation definition`, or a `presentation definition` object.

It is an error to specify both attributes. The value of these attributes identify `Verifiable Presentations` required
for issuance or re-issuance.

The following are non-normative examples of the `scope` attribute:

```json
{
  "iatp:issuancePolicy": {
    "iatp:scope": [
      "example_scope1",
      "example_scope2"
    ]
  }
}
```

and

```json
{
  "iatp:issuancePolicy": {
    "iatp:scope": "example_scope1"
  }
}
```

The following is a non-normative examples of the `presentationDefinitiion` attribute:

```json
{
  "iatp:issuancePolicy": {
    "iatp:presentationDefinition": {
      "@id": "https://expample.com/example_definition"
    }
  }
}
```

and

```json
{
  "iatp:issuancePolicy": {
    "iatp:presentationDefinition": {
      "id": "example_presentation_definition",
      "input_descriptors": [
        "..."
      ]
    }
  }
}
```
