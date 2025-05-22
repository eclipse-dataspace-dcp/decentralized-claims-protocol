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

The following sequence diagram depicts a non-normative flow where a client interacts with a [=Verifier=] to present a
[=Verifiable Credential=]:

![Presentation Flow](specifications/auth.flow.svg "Presentation Flow")

1. The client sends a request to its [=Secure Token Service=] for a token including an access token. This could be a
   [=Self-Issued ID Token=]. The API used to make this request is implementation specific. The client may include a set
   of scopes that define the [=Verifiable Credentials=] the client wants the [=Verifier=] to have access to. This set of
   scopes is determined out of band and may be derived from metadata the [=verifier=] has previously made available to
   the client.
2. The [=Secure Token Service=] responds with an access token a that may be in `token` claim a [=Self-Issued ID Token=].
   The access token can be used by the verifier to request [=Verifiable Credentials=] from the client's
   [=Credential Service=].
3. The client makes a request to the [=Verifier=] for a protected resource and includes a [=Self-Issued ID Token=]
   containing the access token.
4. The [=Verifier=] resolves the client [=DID=] based on the value of the [=Self-Issued ID Token=] `sub` claim.
5. The [=DID Service=] returns the DID Document. The [=Verifier=] validates the [=Self-Issued ID Token=] following
   Section [[[#validating-self-issued-id-tokens]]].
6. The [=Verifier=] obtains the client's [=Credential Service=] endpoint address using the DID document as described in
   Section [[[#credential-service-endpoint-discovery]]]. The [=Verifier=] then issues a request with the access token
   to the [=Credential Service=] for a set of [=Verifiable Credentials=].
7. The [=Credential Service=] validates the access token and returns a [=Verifiable Presentation=] containing the
   requested credentials.

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
    "https://w3id.org/dspace-dcp/v1.0/dcp.jsonld"
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

A `PresentationQueryMessage` MUST contain either a `presentationDefinition` or a `scope` parameter. If both parameters 
are present it is an error and the client MUST return an `HTTP 400 BAD REQUEST` response.

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

An implementation MAY support the `presentationDefinition` parameter. If it does not, it MUST return
`501 Not Implemented`. The `presentationDefinition` parameter contains a valid `Presentation Definition`
according to
the [Presentation Exchange Specification](https://identity.foundation/presentation-exchange/spec/v2.1.1/#presentation-definition).
The [=Credential Service=] will use the presentation definition to return a set of matching VPs in the format specified
by the definition.

#### Scopes

Implementations MAY support requesting presentation of [=Verifiable Credentials=] using a set of scope values.
A scope is an alias for a well-defined Presentation Definition. The specific scope value, and the
mapping between it and the respective Presentation Definition is out of scope of this specification.

A scope is a string value in the form:

`[alias]:[descriminator]`

The `[alias]` value MAY be implementation-specific.

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
| **Optional** | - `presentationSubmission`: A valid `Presentation Submission` according to [[presentation-ex]].                                                                                   |

A `PresentationResponseMessage` SHOULD only include valid (non-expired, non-revoked, non-suspended) credentials.
The following are non-normative examples of the JSON response body:

<aside class="example" title="Presentation Response Message">
    <pre class="json" data-include="./resources/presentation/example/presentation-response-message.json">
    </pre>
</aside>

<aside class="example" title="Presentation Response Message with Presentation Submission">
    <pre class="json" data-include="./resources/presentation/example/presentation-response-message-w-presentation-submission.json">
    </pre>
</aside>

#### Presentation Submissions

Implementations that support the `presentationDefinition` parameter MUST include the `presentationSubmission` parameter
in the [[[#presentation-response-message]]] with a
valid [Presentation Submission](https://identity.foundation/presentation-exchange/spec/v2.1.1/#presentation-submission)
when a `presentationDefinition`
is provided in the [[[#presentation-query-message]]].

### Presentation Validation
[=Verifier=] SHOULD validate the [=Verifiable Presentation=] in the following manner:

1. The [=Verifier=] MUST assert that the [=Verifiable Presentation=] is created either according to the `scope` or `presentationDefinition`
2. The [=Verifier=] MUST validate the signature of the [=Verifiable Presentation=] by using the key obtained from the resolved `VerificationMethod` of the [=Verifiable Presentation=]. [=DID=] resolution is performed according to the [=DID=] Method specified in the [=DID=] part of the `VerificationMethod` of the [=Verifiable Presentation=].
3. The [=Verifier=] MUST validate that the `VerificationMethod` of the [=Verifiable Presentation=] has the `Authentication` [Verification Relationship](https://www.w3.org/TR/did-1.0/#authentication)
4. The [=Verifier=] MUST assert that the [=DID=] in the `VerificationMethod` of the [=Verifiable Credential=] has the same value as the `issuer` of the [=Verifiable Credential=]. 
5. The [=Verifier=] MUST validate the signature of the [=Verifiable Credential=] by using the key obtained from the resolved `VerificationMethod` of the [=Verifiable Credential=]. [=DID=] resolution is performed according to the [=DID=] Method specified in the [=DID=] part of the `VerificationMethod` of the [=Verifiable Presentation=].
6. If the [=Verifiable Credential=] contains a revocation mechanism, such as `StatusList2021`, the [=Verifier=] MUST validate the status of the [=Verifiable Credential=] according to the revocation mechanism. 
7. If the [=Verifiable Presentation=] contains any claims regarding its `expiryDate` or `validity`, the [=Verifier=] MUST validate those claims.
8. If any of the steps fail, the [=Verifier=] MUST consider the [=Verifiable Presentation=] invalid. 

Additionally, if the specific semantics of a data space and credentials require cryptographic holder binding, the [=Verifier=] MUST assert that the `credentialSubject.id` in the [=Verifiable Credential=] and the [=DID=] part of the `VerificationMethod` of the [=Verifiable Presentation=] have the same value.
