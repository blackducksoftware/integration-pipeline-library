package com.blackduck.integration.utilities

class GithubBranchParser {

    GithubBranchParser() {

    }

    com.blackduck.integration.model.GithubBranchModel parseBranch(String branch) {
        if (branch.contains('/')) {
            String[] pieces = branch.split('/')
            if (pieces.length != 2) {
                throw new IllegalArgumentException('The branch provided was not in a valid format.')
            }
            return new com.blackduck.integration.model.GithubBranchModel(pieces[0], pieces[1])
        }
        return new com.blackduck.integration.model.GithubBranchModel('origin', branch)
    }
}
