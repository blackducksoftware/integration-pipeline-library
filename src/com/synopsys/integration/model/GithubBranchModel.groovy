package com.synopsys.integration.model

class GithubBranchModel {
    public final String remote
    public final String branchName

    GithubBranchModel(final String remote, final String branchName) {
        this.remote = remote
        this.branchName = branchName
    }

    String getRemote() {
        return remote
    }

    String getBranchName() {
        return branchName
    }
}
