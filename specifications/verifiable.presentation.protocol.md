# Verifiable Presentation Protocol

This section defines a protocol for storing and presenting [=Verifiable Credentials=] and other identity-related
resources. The Verifiable Presentation Protocol covers the following aspects:

- Endpoints and message types for storing identity resources belonging to a [=Holder=] in a [=Credential Service=]
- Endpoints and message types for resolving an identity [=Resource=]
- Secure token exchange for restricting access to  [=Credential Service=] endpoints

<aside class="note">
The Verifiable Presentation Protocol is designed to address the problem of resolving [=Verifiable Presentations=] and
other claims when they cannot be passed as part of a client request. For example, they often cannot be included as part
of an HTTP message header due to size restrictions. The protocol provides a secure mechanism for a [=Verifier=] to 
resolve credential-related resources. The protocol also provides a mechanism for storing issued [=Verifiable Credentials=].   
</aside>

## Presentation Flow

The following sequence diagram depicts a normative flow where a client interacts with a [=Verifier=] to present a [=Verifiable Credential=]. This flow MUST be followed for consistent interoperability:

![alt text 2](specifications/auth.flow.png "Presentation Flow")

1. Client → STS: The client MUST send a request to its [=Secure Token Service=] for a [=Self-Issued ID Token=]. 
   The client MUST include any relevant scope values—derived from metadata the [=Verifier=] provides out of band—to 
   indicate the set of [=Verifiable Credentials=] that the [=Verifier=] is authorized to request.
2. STS → Client: The [=Secure Token Service=] MUST respond with a Self-Issued ID token that contains a token claim. 
   This token claim MUST be set to an access token the [=Verifier=] can later use to request [=Verifiable Credentials=] 
   from the client’s [=Credential Service=].
3. Client → Verifier: The client MUST make a request to the [=Verifier=] for a protected resource and MUST include 
   the [=Self-Issued ID Token=].
4. Verifier → DID Resolution: Upon receiving the [=Self-Issued ID Token=], the [=Verifier=] MUST resolve the 
   client’s [=DID=] by reading the sub claim in the ID token and performing a DID resolution process through a [=DID Service=].
5. DID Service → Verifier: The [=DID Service=] MUST return the DID Document to the [=Verifier=]. The [=Verifier=] MUST validate 
   the [=Self-Issued ID Token=] according to [Section [[[#validating-self-issued-id-tokens]]]].
6. Verifier → Credential Service: The [=Verifier=] MUST obtain the client’s [=Credential Service=] endpoint address from the DID Document, 
   per [Section [[[#credential-service-endpoint-discovery]]]]. The [=Verifier=] MUST then make a request to the [=Credential Service=] 
   using the access token to retrieve the needed [=Verifiable Credentials=].
7. Credential Service → Verifier: The [=Credential Service=] MUST validate the access token. If valid, it MUST return a [=Verifiable Presentation=] 
   containing the requested [=Verifiable Credentials=].


## Credential Service Endpoint Discovery

The client [=DID Service=] MUST make the [=Credential Service=] available as a `service` entry ([[[did-core]]], sect.
5.4) in the DID document that is resolved by its [=DID=]. The `type` attribute of the `service` entry MUST be
`CredentialService`. The `serviceEndpoint` property MUST be interpreted by the Verifier as the base URL of
the [=Credential Service=]. The following is a non-normative example of a `Credential Service` entry:

<aside class="example" title="Credential Service Entry in DID document">
    <pre class="json">
{
  "@context": [
    "https://www.w3.org/ns/did/v1",
    "https://w3id.org/dspace-dcp/v1.0"
  ],
  "service": [
    {
      "id": "did:example:123#identity-hub",
      "type": "CredentialService",
      "serviceEndpoint": "https://cs.example.com"
    }
  ]
}
    </pre>
</aside>

## Credential Service Security

As described in the previous presentation flow, a [=Credential Service=] may require an access token when processing a
request from a [=Verifier=]. The format of the access token is not defined. How access control is defined in
a [=Credential Service=] is implementation-specific. For example, implementations may provide the ability to selectively
restrict access to resources.

### Submitting an Access Token

Implementations that support access control require an access token. To provide the opportunity
for [=Credential Service=] implementations to enforce proof-of-possession, the access token MUST be contained in the
`token` claim of a Self-Issued ID Token as defined in Section [[[#self-issued-id-tokens]]]. The [=Self-Issued ID Token=]
MUST be submitted in the HTTP `Authorization` header prefixed with `Bearer` of the request.

## Resolution API

The Resolution API defines the [=Credential Service=] endpoint for querying credentials and returning a set
of [=Verifiable Presentations=].

If a client is not authorized for an endpoint request, the [=Credential Service=] SHOULD return `4xx Client Error`. The
exact error code is implementation-specific.

|                 |                                                                                          |
|-----------------|------------------------------------------------------------------------------------------|
| **Sent by**     | [=Verifier=]                                                                             |
| **HTTP Method** | `POST`                                                                                   |
| **URL Path**    | `/presentations/query`                                                                   |
| **Request**     | [`PresentationQueryMessage`](#presentation-query-message)                                |
| **Response**    | [`PresentationResponseMessage`](#presentation-response-message) `HTTP 2xx` OR `HTTP 4xx` |

### Presentation Query Message

|              |                                                                                                 |
|--------------|-------------------------------------------------------------------------------------------------|
| **Schema**   | [JSON Schema](./resources/presentation/presentation-query-message-schema.json)                  |
| **Required** | - `@context`: Specifies a valid Json-Ld context ([[json-ld11]], sect. 3.1)                      |
|              | - `type`: A string specifying the `PresentationQueryMessage` type.                              |
| **Optional** | - `scope`: An array of scopes corresponding to Section [[[#scopes]]].                           |
|              | - `presentationDefinition`: A valid `Presentation Definition` according to [[presentation-ex]]. |

A `PresentationQueryMessage` MUST contain either a `presentationDefinition` or a `scope` parameter. It is an error to
contain both.

The following are non-normative examples of the JSON body:

<aside class="example" title="PresentationQueryMessage with scope">
    <pre class="json" data-include="./resources/presentation/example/presentation-query-message.json">
    </pre>
</aside>

<aside class="example" title="PresentationQueryMessage with presentationDefinition">
    <pre class="json" data-include="./resources/presentation/example/presentation-query-message-w-presentation-definition.json">
    </pre>
</aside>

#### Presentation Definitions

An implementations MAY support the `presentationDefinition` parameter. If it does not, it MUST return
`501 Not Implemented`. The `presentationDefinition` parameter contains a valid `Presentation Definition`
according to
the [Presentation Exchange Specification](https://identity.foundation/presentation-exchange/spec/v2.0.0/#presentation-definition).
The [=Credential Service=] will use the presentation definition to return a set of matching VPs in the format specified
by the definition.

#### Scopes

Implementations MAY support requesting presentation of [=Verifiable Credentials=] using a set of scope values.
A scope is an alias for a well-defined Presentation Definition. The specific scope value, and the
mapping between it and the respective Presentation Definition is out of scope of this specification.

A scope is a string value in the form:

`[alias]:[descriminator]`

The `[alias]` value MAY be implementation-specific.

<aside class="example" title="Requesting a Token with Scopes">
    <pre class="text" data-include="./resources/presentation/example/presentation.scope-alias.txt"></pre>
</aside>

##### The `org.eclipse.dspace.dcp.vc.type` Alias

The `vc.type` alias value MUST be supported and is used to specify access to a verifiable credential by type. For
example:

`org.eclipse.dspace.dcp.vc.type:Member`

denotes read-only access to the VC type `Member` and may be used to request a VC or VP.

##### The `org.eclipse.dspace.dcp.vc.id` Alias

The `org.eclipse.dspace.dcp.vc.id` alias value must be supported and is used to specify access to a verifiable
credential by id. For example:

`org.eclipse.dspace.dcp.vc.id:8247b87d-8d72-47e1-8128-9ce47e3d829d`

denotes read-only access to the VC identified by `8247b87d-8d72-47e1-8128-9ce47e3d829d` and may be used to request a
[=Verifiable Credential=].

### Presentation Response Message

|              |                                                                                                                                                                                   |
|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Schema**   | [JSON Schema](./resources/presentation/presentation-response-message-schema.json)                                                                                                 |
| **Required** | - `@context`: Specifies a valid Json-Ld context ([[json-ld11]], sect. 3.1).                                                                                                       |
|              | - `type`: A string specifying the `PresentationResponseMessage` type.                                                                                                             |
|              | - `presentation`: An array of [=Verifiable Presentations=]. The [=Verifiable Presentations=] may be strings, JSON objects, or a combination of both depending on the format.</br> |

A `PresentationResponseMessage` SHOULD only include valid (non-expired, non-revoked, non-suspended) credentials.
The following are non-normative examples of the JSON response body:

<aside class="example" title="Presentation Response Message">
    <pre class="json" data-include="./resources/presentation/example/presentation-response-message.json">
    </pre>
</aside>