# 1. Introduction

This document defines a profile of
the [Dataspace Protocol Specifications (DSP)](https://github.com/International-Data-Spaces-Association/ids-specification).

# Catalog Metadata for the __Well-Known__ Endpoint

Catalogs that support the [Credential Issuance Protocol (CIP)](credential.issuance.protocol.md) MUST return a JSON
object from the `/.well-known/dspace-trust` endpoint with the following properties:

- `@context`: REQUIRED. Specifies a valid [Json-Ld context](https://www.w3.org/TR/json-ld11/#the-context).
- `credentialsSupported`: REQUIRED. Contains a Json structure corresponding to the schema
  specified [below](#the-credentialssupported-object).

A non-normative example is:

```json
{
  "@context": [
    ...
  ],
  "credentialsSupported": [
    {
      "types": [
        "VerifiableCredential",
        "CompanyCredential"
      ],
      "bindingMethod": "did:web",
      "cryptographicSuite": "JsonWebSignature2020"
    }
  ]
}

```

### The `CredentialsSupported` Object

The credentials supported contains a single Json Object or an Array of each verifiable credential (VC) supported by the
catalog. The `CredentialsSupported` object contains the following properties:

- `types`: REQUIRED. An array of verifiable credential type strings the credential corresponds to
- `bindingMethods`: REQUIRED. Array of strings that identify how the credential is bound to the identifier of the
  credential holder.
- `cryptographicSuites` REQUIRED. An array of strings that identify the cryptographic suites supported for verifying
  proofs. Values should either use those defined
  by [IANA JOSE](https://www.iana.org/assignments/jose/jose.xhtml#web-signature-encryption-algorithms) for JWT-based VCs
  or the [Linked Data Cryptographic Suite Registry](https://w3c-ccg.github.io/ld-cryptosuite-registry/) for LD-based
  VCs.
