# Dataspace Ecosystem Overview

This section describes the core actors and systems in a dataspace ecosystem that are pertinent to this specification. It
adheres to the model defined by the Dataspace Protocol [[dsp-base]]:

- A <dfn>Dataspace</dfn> is a policy-based data sharing context between two or more entities.
- The <dfn>Dataspace Authority</dfn> is responsible for operational management of a [=dataspace=],
  including [=participant=] registration and designation of trust credential issuers.
- A <dfn>Participant</dfn> is a member of the [=dataspace=]. Members may take on different roles, which are attested to
  by verifiable credentials.
- A <dfn>Participant Agent</dfn> performs tasks such as publishing a catalog or engaging in data transfer. Note that a
  participant agent is a logical construct and does not necessarily correspond to a single runtime process.
- An <dfn>Identity Provider</dfn> is a service that generates identity tokens used to verify the identity of a
  [=Participant Agent=]. Each [=participant=] will use their own identity provider to generate a self-signed identity
  token.
- A <dfn>Credential Issuer</dfn> issues [=Verifiable Credentials=] used by [=participant agents=] to allow access to
  assets and verify usage control.

## Systems

### Dataspace Authority Systems

<dfn data-lt="rs | Registration System">Registration System</dfn>

The registration system is responsible for [=participant=] registration, onboarding, and management in a dataspace.
The registration system is run by the [=Dataspace Authority=] and is outside the scope of this specification.

### Participant Agent Systems

[=Participants=] will run one or more agent systems that interact in the dataspace. These systems may offer data
catalogs, perform data transfers, or provide application functionality. A [=participant=] may run the following
identity-related agents. Note that this is a logical description and may not represent an actual deployment topology.

<dfn data-lt="sts | Secure Token Service">Security Token Service (STS).</dfn>

The STS creates self-issued authorization tokens that contain identity claims used by [=participant agents=] under the
control of the same [=participant=].

<dfn data-lt="cs | Credential Service">Credential Service (CS)</dfn>

The CS manages [=Verifiable Credentials=]. This includes read and write endpoints for [=Verifiable Presentations=]
and [=Verifiable Credentials=].

<dfn data-lt="dids | DID Service">DID Service (DIDS).</dfn>

The DIDS creates, signs and publishes DID documents.

### Credential Issuer Systems

<dfn data-lt="is | Issuer Service">Issuer Service (IS)</dfn>

The Issuer Service is run by trust anchor and manages the lifecycle of [=Verifiable Credentials=] in a dataspace. A
dataspace may contain multiple Issuer Services run by different trust anchors. The Issuer Service:

- Issues  [=Verifiable Credentials=] for dataspace [=participants=].
- Manages revocation lists for  [=Verifiable Credentials=] types it issues based
  on [[[vc-bitstring-status-list-20230427]]].
- Provides cryptographic material used to verify  [=Verifiable Presentations=] and [=Verifiable Credentials=]. 
