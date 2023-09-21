# Etiquette for pull requests

Typically, changes to the specification documents should be made by
the [editors](#the-designated-editors-as-of-sept-21-2023), as it is their duty to ensure consistency and proper
linguistic properties. However, if you want to raise pull-requests of your own, please be sure to carefully read the
following sections, and adhere to the rules stated therein. Failing to do so may result in the rejection of your PR.

## As an author

Submitting pull requests in EDC should be done while adhering to a couple of simple rules.

- Familiarize yourself with the contribution guidelines.
- No surprise PRs please. Before you submit a PR, open a discussion or an issue outlining your planned work and give
  people time to comment. It may even be advisable to contact committers using the `@mention` feature. Unsolicited PRs
  may get ignored or rejected.
- Don't argue basic principles ("Why don't we use X instead of Y", "how-about-ism")
- Create your working branch in your fork of this repository, and create the PR against the upstream `main` branch
- Create focused PRs: your work should be focused on one particular feature or bug. Do not create broad-scoped PRs that
  solve multiple issues as reviewers may reject those PR bombs outright.
- Provide a clear description and motivation in the PR description in GitHub. This makes the reviewer's life much
  easier. It is also helpful to outline the broad changes that were made, e.g. "Changes the wording of XYZ-Entity:
  use `XYZ` instead of `ABC`".
- All CI checks should be green, especially when your PR is in `"Ready for review"`
- Mark PRs as `"Ready for review"` only when you're prepared to defend your work. By that time you have completed your
  work and shouldn't need to push any more commits other than to incorporate review comments.
- Merge conflicts should be resolved by squashing all commits on the PR branch, rebasing onto `main` and
  force-pushing. Do this when your PR is ready to review.
- If you require a reviewer's input while it's still in draft, please contact the designated reviewer using
  the `@mention` feature and let them know what you'd like them to look at.
- Request a review from one of the committers. Requesting a review from anyone else is still possible, and
  sometimes may be advisable, but only committers can merge PRs, so be sure to include them early on.
- Re-request reviews after all remarks have been adopted. This helps reviewers track their work in GitHub.
- If you disagree with a committer's remarks, feel free to object and argue, but if no agreement is reached, you'll have
  to either accept the decision or withdraw your PR.
- Be civil and objective. No foul language, insulting or otherwise abusive language will be tolerated.
- The PR titles must follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/).
    - The title must follow the format as `<type>(<optional scope>): <description>`.
      `build`, `chore`, `ci`, `docs`, `feat`, `fix`, `perf`, `refactor`, `revert`, `style`, `test` are allowed for
      the `<type>`.
    - The length must be kept under 80 characters.

## As a reviewer

- Please complete reviews within two business days or delegate to another committer, removing yourself as a reviewer.
- If you have been requested as reviewer, but cannot do the review for any reason (lack of time or expertise in a
  particular area, etc.) please comment that in the PR and remove yourself as a reviewer, suggesting a stand-in.
- Be pedantic. This is a normative document, so wording, terminology and general formulation are important.
- Don't get dragged into basic arguments ("how-about-ism")
- Use the `suggestion` feature of GitHub for small/simple changes.
- The following could serve you as a review checklist:
    - writing style, wording
    - simplicity and "uncluttered-ness" of the text
    - overall focus of the PR
- Don't just wave through any PR. Please take the time to look at them carefully.
- Be civil and objective. No foul language, insulting or otherwise abusive language will be tolerated. The goal is to
  _encourage_ contributions.

## The designated editors (as of Sept 21, 2023)

- Jim Marino (@jimmarino)
- Matthias Binzer (@matgnt)

## Responsible committers (as of Sept 21, 2023)

- Paul Latzelsperger (@paullatzelsperger)
- Enrico Risa (@wolf4ood)
