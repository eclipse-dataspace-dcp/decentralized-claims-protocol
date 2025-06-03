# Terminology

The following terms are used to describe concepts in this specification.

- <dfn data-lt="Claim | Claims">Claim</dfn> - An assertion made about a [=Subject=].
- <dfn>DID</dfn> - A decentralized identifier as defined by [[[did-core]]].
- <dfn>Holder</dfn> - An entity that possesses a set of identity resources as defined by the specific VC DataModel that is mandated by the profile [[[vc-data-model-2.0]]].
  The holder will typically be the subject of a [=Verifiable Credential=].
- <dfn>Issuer</dfn> - An entity that asserts [=claims=] about one or more subjects by issuing
  a [=Verifiable Credential=] to a [=Holder=].
- <dfn>Resource</dfn> - A resource is an entity managed by the Credential Service such as a [=Verifiable Credential=]
  or [=Verifiable Presentation=].
- <dfn>Subject</dfn> - The target of a set of claims contained in a [=Verifiable Credential=] as defined
  by [[[vc-data-model-2.0]]]. In a dataspace, a subject will be a participant.
- <dfn data-lt="Verifiable Credential | Verifiable Credentials">Verifiable Credential</dfn> A tamper-evident credential
  whose authorship can be cryptographically verified as defined by the specific VC DataModel that is mandated by the profile [[[vc-data-model-2.0]]].
- <dfn data-lt="Verifiable Presentation | Verifiable Presentations">Verifiable Presentation</dfn> A tamper-evident
  presentation of information whose authorship can be cryptographically verified as defined by the specific VC DataModel that is mandated by the profile [[[vc-data-model-2.0]]].
- <dfn>Verifiable Data Registry</dfn> - Maintains identifiers such as [=DIDs=] and [=Verifiable Credential=] schemas in
  a dataspace.
- <dfn>Verifier</dfn> - An entity that receives a [=Verifiable Credential=], optionally presented inside
  a [=Verifiable Presentation=] as defined by [[[vc-data-model-2.0]]].
