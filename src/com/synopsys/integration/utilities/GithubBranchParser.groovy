package com.synopsys.integration.utilities

import com.synopsys.integration.model.GithubBranchModel

class GithubBranchParser {

    GithubBranchParser() {

    }

    GithubBranchModel parseBranch(String branch) {
        if (branch.contains('/')) {
            String[] pieces = branch.split('/')
            if (pieces.length != 2) {
                throw new IllegalArgumentException('The branch provided was not in a valid format.')
            }
            return new GithubBranchModel(pieces[0], pieces[1])
        }
        return new GithubBranchModel('origin', branch)
    }
}
