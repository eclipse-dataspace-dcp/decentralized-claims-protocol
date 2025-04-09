# Trust Model and Information Flow Architecture

This specification is based on the Issuer-Holder-Verifier model as described in the [[[vc-data-model]]]:

![alt text 2](specifications/issuer-holder-verifier-model.svg "The Issuer-Holder-Verifier Model")

In this model, the [=Issuer=] creates a [=Verifiable Credential=] and provides it to the [=Holder=], which can then
present the [=Verifiable Credential=] to the [=Verifier=].

The [=Verifier=] decides which [=Verifiable Credentials=] it will accept and the [=Holder=] decides
which [=Verifiable Credentials=] it will present. All parties rely on a [=Verifiable Data Registry=] to obtain
identifier information and metadata about [=Verifiable Credentials=], such as
schemas. Furthermore, parties communicate with each other using the protocols defined in the Dataspace Protocol
specification [[dsp-base]] and this specification.

The Issuer-Holder-Verifier model is mapped to the following architecture in this specification:

![alt text 2](specifications/participant-agent-model.svg "Information Flow Architecture")

- Identity and claims information is exchanged between a [=Holder=] and a [=Verifier=] in the context of Dataspace
  Protocol message interactions [[dsp-base]]. These messages will contain metadata such as Offers and Agreements that
  determine the [=Verifiable Credentials=] a [=Holder=] must present to a [=Verifier=].
- The [=Issuer Service=] issues credentials to a [=Holder=]'s [=Credential Service=] using the protocol defined in
  Section [[[#credential-issuance-protocol]]]. As part of this process, the [=Issuer Service=] will verify
  the [=Holder=]'s identifier using the [=Verifiable Data Registry=]. Since [=DID=]s are mandatory in this
  specification, the registry will be used to resolve the [=Holder=]'s DID document and cryptographic material.
- The [=Holder=] runs three logical services: a [=Participant Agent=], an [=STS=], and a [=Credential Service=].
  The [=Participant Agent=] engages in dataspace communications with the [=Verifier=] agent [[dsp-base]]. As part of
  these interactions, the [=Participant Agent=] will include a Self-Issued ID Token as defined in
  Section [[[#self-issued-id-tokens]]]. This token will be obtained from the [=STS=] controlled by the [=Holder=].
  **Note that the [=STS=] is an internal system and therefore out of scope for the current specification. It may be a
  standalone service or part of the [=Participant Agent=] or [=Credential Service=].** The Self-Issued ID Token will
  contain an access token bound to the [=Verifier=] that will be used to request a [=Verifiable Presentation=].
  The [=Holder=]'s [=Credential Service=] will verify the access token with the [=STS=].
- The [=Verifier=] runs a [=Participant Agent=] that engages in dataspace message exchanges with the [=Holder=]
  agent [[dsp-base]]. When it receives a request, it will resolve the [=Holder=]'s DID document from
  the [=Verifiable Data Registry=]. The [=Participant Agent=] will resolve the [=Credential Service=] endpoint from the
  DID document. The [=Participant Agent=] will send a request for a [=Verifiable Presentation=] to
  the [=Credential Service=] using the protocol defined in Section [[[#verifiable-presentation-protocol]]]. This
  request will include the access token as a claim in the Self-Issued ID Token received from the [=Holder=]'s agent.
  The [=Participant Agent=] will then match the credentials returned in the [=Verifiable Presentation=] against the
  policy constraints associated with the original dataspace request.

## Consent

Consent is the process where the [=Holder=] approves the release of a [=Verifiable Presentation=] and the [=Verifier=]
agrees to allow access to a protected resource. Since the Dataspace Protocol is built on machine-to-machine message
exchange patterns, presentation protocols that rely on end-user (i.e., human) consent are not applicable. The protocols
defined in this specification establish consent in the following way:

![alt text 3](specifications/consent-model.svg "Consent in the Decentralized Claims Protocol")

Consent is handled by first mapping Dataspace Protocol Offer and Agreement policies [[dsp-base]] to a set of
required [=Verifiable Credentials=]. The exact mappings are dataspace-specific as policies
and [=Verifiable Credentials=] are typically defined for a particular domain. The expectation is that these mappings are
made available by the [=Dataspace Governance Authority=] to [=Participant Agents=] via
the [=Verifiable Data Registry=]. [=Verifiable Credential=] mapping is done by both the [=Holder=]
and [=Verifier=] [=Participant Agent=] when a Dataspace Protocol message is sent and received.

When sending a Dataspace Protocol message, the [=Holder=]'s [=Participant Agent=] will determine the required
[=Verifiable Credentials=] and issue a Self-Issued ID Token request to the [=STS=], including the list of credentials to
allow the [=Verifier=] to access via the [=Credential Service=]. The returned Self-Issued ID token will contain an
access token bound to the [=Verifier=] as described above. This access token serves as a verifiable mechanism
the [=Credential Service=] uses to determine consent and release credentials to the [=Verifier=].

The [=Verifier=] establishes consent by performing the same mapping from metadata to a set
of [=Verifiable Credentials=] during the initial Dataspace Protocol request. At that point,
the [=Verifier=] [=Participant Agent=] can query the [=Credential Service=] for a [=Verifiable Presentation=]. If it
matches the required credentials, the [=Verifier=] can consent and grant access to the associated protected resources.

## Trust Relationships

Trust in the Issuer-Holder-Verifier model is established in different ways between each entity. This section identifies
key aspects of those trust relationships.

**All Entities**

All entities expect:

- The [=Verifiable Data Registry=] to be tamper-evident and correctly reflect data controlled by all entities. This may
  be done through the use of cryptographic resources.
- All [=Verifiable Credentials=] and [=Verifiable Presentations=] to be tamper-proof and support revocation.
- Dataspace Protocol interactions and those defined in this specification to be secure and use Transport Level
  Security.

**Issuer**

The [=Issuer=] expects:

- [=Credential Service=] integrity, i.e. that [=Verifiable Credentials=] are securely stored and the service does not
  reveal data to unauthorized parties.

**Holder**

The [=Holder=] expects:

- The [=Dataspace Governance Authority=] to provide a secure list of trusted [=Issuer=] [=DID=]s. How this list is provided is out
  of scope of the current specification but may be part of the [=Verifiable Data Registry=].
- The [=Dataspace Governance Authority=] to provide a secure list of participant [=DIDs=]. How this list is provided is out
  of scope of the current specification but may be part of the [=Verifiable Data Registry=].
- The [=Credential Service=] to maintain integrity and not release data to unauthorized parties.
- The [=Issuer=] to issue credentials correctly, including binding them to the holder in a tamper-proof way, and
  maintaining credential integrity.
- The [=Verifier=] not to leak information to third-parties.

**Verifier**

The [=Verifier=] expects:

- The [=Dataspace Governance Authority=] to provide a secure list of trusted [=Issuer=] [=DID=]s. How this list is provided is out
  of scope of the current specification but may be part of the [=Verifiable Data Registry=]. [=Verifiers=] can
  recognize several [=Dataspace Governance Authorities=].

- The [=Issuer=] to issue credentials correctly, including binding them to the holder in a tamper-proof way, and
  maintaining credential integrity.

## Transport Security

All participants MUST communicate via HTTPS [[rfc9110]].
