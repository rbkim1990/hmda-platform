[![Build Status](https://travis-ci.org/cfpb/hmda-platform.svg?branch=master)](https://travis-ci.org/cfpb/hmda-platform) [![codecov.io](https://codecov.io/github/cfpb/hmda-platform/coverage.svg?branch=master)](https://codecov.io/github/cfpb/hmda-platform?branch=master)

# HMDA Platform

## Introduction

For more information on HMDA, checkout the [About HMDA page](http://www.consumerfinance.gov/data-research/hmda/learn-more) on the CFPB website.

## The HMDA Platform

This repository contains the code for the entirety of the `HMDA Platform` backend. This platform has been designed to accommodate the needs of the HMDA filing process by financial institutions, as well as the data management and publication needs of the HMDA data asset.
The code contained in this repository covers two distinct versions of the HMDA backend, referred to as `v1` (2017 data collection) and `v2` (2018 and later data collection).

The documentation for each version is linked below:

* [HMDA Platform v1 Documentation](docs/v1/README.md)

* [HMDA Platform v2 Documentation](docs/v2/README.md)

The main `GitHub` branch, `master`, points to the latest version of the HMDA Platform. Previous versions at available at the following branches:

* [HMDA Platform v1 (2017 Data Collection)](https://github.com/cfpb/hmda-platform/tree/v1.x)

## Postman Collection

The [HMDA Postman](https://github.com/cfpb/hmda-platform/tree/master/newman/postman) collection is located inside the `newman/postman` folder of this repo. The collection has everything you need to file you HMDA data using API endpoints.

## Contributing

`CFPB` is developing the `HMDA Platform` in the open to maximize transparency and encourage third party contributions. If you want to contribute, please read and abide by the terms of the [License](LICENSE) for this project.

We use GitHub issues in this repository to track features, bugs, and enhancements to the software. [Pull Requests](https://help.github.com/articles/using-pull-requests/) are welcome

## Open source licensing info
1. [TERMS](TERMS.md)
2. [LICENSE](LICENSE)
3. [CFPB Source Code Policy](https://github.com/cfpb/source-code-policy/)

## Credits and references

Related projects
  - https://github.com/cfpb/hmda-frontend
  - https://github.com/cfpb/hmda-help
  - https://github.com/cfpb/hmda-platform-auth
  - https://github.com/cfpb/hmda-platform-larft
  - https://github.com/cfpb/hmda-test-files
  - https://github.com/cfpb/hmda-documentation
  - https://github.com/cfpb/hmda-census
  - https://github.com/cfpb/hmda-platform-api-docs
