# Contributing guidelines

## How to become a contributor and submit your own code

### Contributor License Agreements

We'd love to accept your patches!!!

***NOTE***: Only original source code from you and other people that signed the CLA can be accepted into the main repository.

### Contributing code

If you have improvements please, send us your pull requests! For those
just getting started, Github has a [howto](https://help.github.com/articles/using-pull-requests/).

The team members will be assigned to review your pull requests. Once the pull requests are approved and pass continuous integration checks, we will merge the pull requests.
For some pull requests, we will apply the patch for each pull request to our internal version control system first, and export the change out as a new commit later, at which point the original pull request will be closed. The commits in the pull request will be squashed into a single commit with the pull request creator as the author. These pull requests will be labeled as pending merge internally.

If you want to contribute but you're not sure where to start, take a look at the
[issues with the "contributions welcome" label].
These are issues that we believe are particularly well suited for outside
contributions, often because we probably won't get to them right now. If you
decide to start on an issue, leave a comment so that other people know that
you're working on it. If you want to help out, but not alone, use the issue
comment thread to coordinate.

### Contribution guidelines and standards

Before sending your pull request for
[review](https://github.com/kyoubee/MonitoringApp/pulls),
make sure your changes are consistent with the guidelines and follow the
appropriate coding style.

#### General guidelines and philosophy for contribution

* Include unit tests when you contribute new features, as they help to
  a) prove that your code works correctly, b) guard against future breaking
  changes to lower the maintenance cost.
* Bug fixes also generally require unit tests, because the presence of bugs
  usually indicates insufficient test coverage.
* When you contribute a new feature, the maintenance burden is (by
  default) transferred to the our team. This means that benefit of
  contribution must be compared against the cost of maintaining the feature.

#### License

Include a license at the top of new files.

#### Coding style for other languages

* [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
* [Google JavaScript Style Guide](https://google.github.io/styleguide/jsguide.html)
* [Google Shell Style Guide](https://google.github.io/styleguide/shell.xml)
* [Google Objective-C Style Guide](https://google.github.io/styleguide/objcguide.html)