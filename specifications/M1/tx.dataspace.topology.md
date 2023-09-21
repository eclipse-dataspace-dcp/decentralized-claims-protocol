# 1. Introduction

This document details the systems topology that Tractus-X dataspaces employ. This topology adheres to the model defined
by the [Dataspace Protocol Specifications](https://docs.internationaldataspaces.org/dataspace-protocol/overview/model) (
DSP):

- The **Dataspace Authority** manages the dataspace. In a Tractus-X dataspace, this role may be federated across
  multiple operating companies responsible for registration, onboarding, and operations management.
- A **Participant** is a member the dataspace. In a Tractus-X dataspace, members may take on different roles, which are
  attested to by **Verifiable Credentials**.
- A **Participant Agent** performs tasks such as publishing a catalog or engaging in an asset transfer. Note that a
  participant agent is a logical construct and does not necessarily correspond to a single runtime process.
- An **Identity Provider** is a service that generates ID tokens used to verify the identity of a Participant Agent. In
  a Tractus-X dataspace, each participant will use their own identity provider, which may be self-hosted or hosted in a
  managed environment.
- A **Credential Issuer** issues Verifiable Credentials (VC) used by participant agents to allow access to assets and
  verify usage control.

## 1.1. Scope

This specification does not cover dataspace registration systems managed by a Dataspace Authority (operating company) as
the participant registration and discovery process is out-of-scope.

## 1.2. Identities

The DSP specifications are based on the concept that all participants have a stable identifier (ID). This ID is
typically a number assigned to the participant business entity. In this scheme, participant agent identities may be
ephemeral since all operations such as signing `contract agreements`are associated with the participant identity.

This specification will also make use of DIDs, which can be employed to cryptographically verify a participant identity.
These are related as follows:

```
ID  ------ Can resolve to -----> DID
 ^                                |
 |                                |
 |----------Associated with--------                               
```

A DID may also be used as an ID, but it is recommended to distinguish the two to allow for DID rotation. For example, a
participant may opt to change its hosting environment, resulting in a change to its DID such as the URL associated with
its DID in the case of `did:web` or its DID method. Since its Identifier remains stable, existing signed contracts will
not be impacted.

# 2. Systems

## 2.1. Registration System

The registration system is responsible for participant registration, onboarding, and management.

### 2.1.1. Registration Process

When a participant is onboarded, it will be assigned an ID and either a **secret token/code** or **Membership VC** that
will allow the participant to bootstrap its systems. If provided a secret token, the participant will be able to
exchange it for a Membership VC with a Credential Issuer.

## 2.2. Participant Agent Systems

Participants will run one or more agent systems that interact in the dataspace. These systems may offer data catalogs,
perform data transfers or provide application functionality.

A participant may run the following identity-related agents. Note that this is a logical description and may not
represent an actual deployment topology.

### 2.2.1 Security Token Service (STS).

The STS creates self-issued authorization tokens that contain identity claims used by participant agents under the
control of the same participant.

### 2.2.2 Credential Service (CS)

The CS manages [Verifiable Credentials](https://www.w3.org/TR/vc-data-model/). This includes read and write endpoints
for Verifiable Presentations (VPs) and Verifiable Credentials (VCs).

### 2.2.3 DID Service (DIDS).

The DIDS Creates, signs and publishes DID documents.

## 2.3. Issuer Service (IS)

The Issuer Service is run by trust anchor and manages the lifecycle of Verifiable Credentials in a dataspace. A
dataspace may contain multiple Issuer Services run by different trust anchors. The Issuer Service:

- Issues VCs for dataspace participants
- Manages revocation lists for VC types it issues based
  on [Verifiable Credentials Status List v2021](https://www.w3.org/TR/vc-status-list/).
- Provides cryptographic material used to verify VPs and VCs. If this is in the form of a DID, the Issuer Service may
  use the Identity Hub DID Service.  

