package com.sunzk.colortest.update.bean

import org.jetbrains.annotations.TestOnly

data class VersionInfo(
    val assets: List<Asset>,
    val assets_url: String,
    val author: Author,
    val body: String,
    val created_at: String,
    val draft: Boolean,
    val html_url: String,
    val id: Int,
    val immutable: Boolean,
    val name: String,
    val node_id: String,
    val prerelease: Boolean,
    val published_at: String,
    val tag_name: String,
    val tarball_url: String,
    val target_commitish: String,
    val upload_url: String,
    val url: String,
    val zipball_url: String
) {
    companion object {
        @TestOnly
        val testData = "{\n" +
                "\t\"url\": \"https://api.github.com/repos/sunzhk/ColorTest/releases/202464499\",\n" +
                "\t\"assets_url\": \"https://api.github.com/repos/sunzhk/ColorTest/releases/202464499/assets\",\n" +
                "\t\"upload_url\": \"https://uploads.github.com/repos/sunzhk/ColorTest/releases/202464499/assets{?name,label}\",\n" +
                "\t\"html_url\": \"https://github.com/sunzhk/ColorTest/releases/tag/release_v2.7.2\",\n" +
                "\t\"id\": 202464499,\n" +
                "\t\"author\": {\n" +
                "\t\t\"login\": \"sunzhk\",\n" +
                "\t\t\"id\": 10655702,\n" +
                "\t\t\"node_id\": \"MDQ6VXNlcjEwNjU1NzAy\",\n" +
                "\t\t\"avatar_url\": \"https://avatars.githubusercontent.com/u/10655702?v=4\",\n" +
                "\t\t\"gravatar_id\": \"\",\n" +
                "\t\t\"url\": \"https://api.github.com/users/sunzhk\",\n" +
                "\t\t\"html_url\": \"https://github.com/sunzhk\",\n" +
                "\t\t\"followers_url\": \"https://api.github.com/users/sunzhk/followers\",\n" +
                "\t\t\"following_url\": \"https://api.github.com/users/sunzhk/following{/other_user}\",\n" +
                "\t\t\"gists_url\": \"https://api.github.com/users/sunzhk/gists{/gist_id}\",\n" +
                "\t\t\"starred_url\": \"https://api.github.com/users/sunzhk/starred{/owner}{/repo}\",\n" +
                "\t\t\"subscriptions_url\": \"https://api.github.com/users/sunzhk/subscriptions\",\n" +
                "\t\t\"organizations_url\": \"https://api.github.com/users/sunzhk/orgs\",\n" +
                "\t\t\"repos_url\": \"https://api.github.com/users/sunzhk/repos\",\n" +
                "\t\t\"events_url\": \"https://api.github.com/users/sunzhk/events{/privacy}\",\n" +
                "\t\t\"received_events_url\": \"https://api.github.com/users/sunzhk/received_events\",\n" +
                "\t\t\"type\": \"User\",\n" +
                "\t\t\"user_view_type\": \"public\",\n" +
                "\t\t\"site_admin\": false\n" +
                "\t},\n" +
                "\t\"node_id\": \"RE_kwDOEKE2CM4MEVzz\",\n" +
                "\t\"tag_name\": \"release_v2.7.2\",\n" +
                "\t\"target_commitish\": \"v2.7.2\",\n" +
                "\t\"name\": \"release_v2.7.2\",\n" +
                "\t\"draft\": false,\n" +
                "\t\"immutable\": false,\n" +
                "\t\"prerelease\": false,\n" +
                "\t\"created_at\": \"2025-01-22T08:56:23Z\",\n" +
                "\t\"published_at\": \"2025-02-26T05:47:05Z\",\n" +
                "\t\"assets\": [{\n" +
                "\t\t\"url\": \"https://api.github.com/repos/sunzhk/ColorTest/releases/assets/232659769\",\n" +
                "\t\t\"id\": 232659769,\n" +
                "\t\t\"node_id\": \"RA_kwDOEKE2CM4N3hs5\",\n" +
                "\t\t\"name\": \"ColorTest_release_2.7.2.apk\",\n" +
                "\t\t\"label\": null,\n" +
                "\t\t\"uploader\": {\n" +
                "\t\t\t\"login\": \"sunzhk\",\n" +
                "\t\t\t\"id\": 10655702,\n" +
                "\t\t\t\"node_id\": \"MDQ6VXNlcjEwNjU1NzAy\",\n" +
                "\t\t\t\"avatar_url\": \"https://avatars.githubusercontent.com/u/10655702?v=4\",\n" +
                "\t\t\t\"gravatar_id\": \"\",\n" +
                "\t\t\t\"url\": \"https://api.github.com/users/sunzhk\",\n" +
                "\t\t\t\"html_url\": \"https://github.com/sunzhk\",\n" +
                "\t\t\t\"followers_url\": \"https://api.github.com/users/sunzhk/followers\",\n" +
                "\t\t\t\"following_url\": \"https://api.github.com/users/sunzhk/following{/other_user}\",\n" +
                "\t\t\t\"gists_url\": \"https://api.github.com/users/sunzhk/gists{/gist_id}\",\n" +
                "\t\t\t\"starred_url\": \"https://api.github.com/users/sunzhk/starred{/owner}{/repo}\",\n" +
                "\t\t\t\"subscriptions_url\": \"https://api.github.com/users/sunzhk/subscriptions\",\n" +
                "\t\t\t\"organizations_url\": \"https://api.github.com/users/sunzhk/orgs\",\n" +
                "\t\t\t\"repos_url\": \"https://api.github.com/users/sunzhk/repos\",\n" +
                "\t\t\t\"events_url\": \"https://api.github.com/users/sunzhk/events{/privacy}\",\n" +
                "\t\t\t\"received_events_url\": \"https://api.github.com/users/sunzhk/received_events\",\n" +
                "\t\t\t\"type\": \"User\",\n" +
                "\t\t\t\"user_view_type\": \"public\",\n" +
                "\t\t\t\"site_admin\": false\n" +
                "\t\t},\n" +
                "\t\t\"content_type\": \"application/vnd.android.package-archive\",\n" +
                "\t\t\"state\": \"uploaded\",\n" +
                "\t\t\"size\": 21144788,\n" +
                "\t\t\"digest\": null,\n" +
                "\t\t\"download_count\": 21,\n" +
                "\t\t\"created_at\": \"2025-02-26T05:44:40Z\",\n" +
                "\t\t\"updated_at\": \"2025-02-26T05:46:44Z\",\n" +
                "\t\t\"browser_download_url\": \"https://github.com/sunzhk/ColorTest/releases/download/release_v2.7.2/ColorTest_release_2.7.2.apk\"\n" +
                "\t}],\n" +
                "\t\"tarball_url\": \"https://api.github.com/repos/sunzhk/ColorTest/tarball/release_v2.7.2\",\n" +
                "\t\"zipball_url\": \"https://api.github.com/repos/sunzhk/ColorTest/zipball/release_v2.7.2\",\n" +
                "\t\"body\": \"1. 新增找相同颜色模式\\r\\n2. 找不同颜色模式难度优化\\r\\n3. 新增色彩排序模式\\r\\n4. 新增结算动画，优化文案\"\n" +
                "}"
    }
}

/*
{
	"url": "https://api.github.com/repos/sunzhk/ColorTest/releases/202464499",
	"assets_url": "https://api.github.com/repos/sunzhk/ColorTest/releases/202464499/assets",
	"upload_url": "https://uploads.github.com/repos/sunzhk/ColorTest/releases/202464499/assets{?name,label}",
	"html_url": "https://github.com/sunzhk/ColorTest/releases/tag/release_v2.7.2",
	"id": 202464499,
	"author": {
		"login": "sunzhk",
		"id": 10655702,
		"node_id": "MDQ6VXNlcjEwNjU1NzAy",
		"avatar_url": "https://avatars.githubusercontent.com/u/10655702?v=4",
		"gravatar_id": "",
		"url": "https://api.github.com/users/sunzhk",
		"html_url": "https://github.com/sunzhk",
		"followers_url": "https://api.github.com/users/sunzhk/followers",
		"following_url": "https://api.github.com/users/sunzhk/following{/other_user}",
		"gists_url": "https://api.github.com/users/sunzhk/gists{/gist_id}",
		"starred_url": "https://api.github.com/users/sunzhk/starred{/owner}{/repo}",
		"subscriptions_url": "https://api.github.com/users/sunzhk/subscriptions",
		"organizations_url": "https://api.github.com/users/sunzhk/orgs",
		"repos_url": "https://api.github.com/users/sunzhk/repos",
		"events_url": "https://api.github.com/users/sunzhk/events{/privacy}",
		"received_events_url": "https://api.github.com/users/sunzhk/received_events",
		"type": "User",
		"user_view_type": "public",
		"site_admin": false
	},
	"node_id": "RE_kwDOEKE2CM4MEVzz",
	"tag_name": "release_v2.7.2",
	"target_commitish": "v2.7.2",
	"name": "release_v2.7.2",
	"draft": false,
	"immutable": false,
	"prerelease": false,
	"created_at": "2025-01-22T08:56:23Z",
	"published_at": "2025-02-26T05:47:05Z",
	"assets": [{
		"url": "https://api.github.com/repos/sunzhk/ColorTest/releases/assets/232659769",
		"id": 232659769,
		"node_id": "RA_kwDOEKE2CM4N3hs5",
		"name": "ColorTest_release_2.7.2.apk",
		"label": null,
		"uploader": {
			"login": "sunzhk",
			"id": 10655702,
			"node_id": "MDQ6VXNlcjEwNjU1NzAy",
			"avatar_url": "https://avatars.githubusercontent.com/u/10655702?v=4",
			"gravatar_id": "",
			"url": "https://api.github.com/users/sunzhk",
			"html_url": "https://github.com/sunzhk",
			"followers_url": "https://api.github.com/users/sunzhk/followers",
			"following_url": "https://api.github.com/users/sunzhk/following{/other_user}",
			"gists_url": "https://api.github.com/users/sunzhk/gists{/gist_id}",
			"starred_url": "https://api.github.com/users/sunzhk/starred{/owner}{/repo}",
			"subscriptions_url": "https://api.github.com/users/sunzhk/subscriptions",
			"organizations_url": "https://api.github.com/users/sunzhk/orgs",
			"repos_url": "https://api.github.com/users/sunzhk/repos",
			"events_url": "https://api.github.com/users/sunzhk/events{/privacy}",
			"received_events_url": "https://api.github.com/users/sunzhk/received_events",
			"type": "User",
			"user_view_type": "public",
			"site_admin": false
		},
		"content_type": "application/vnd.android.package-archive",
		"state": "uploaded",
		"size": 21144788,
		"digest": null,
		"download_count": 21,
		"created_at": "2025-02-26T05:44:40Z",
		"updated_at": "2025-02-26T05:46:44Z",
		"browser_download_url": "https://github.com/sunzhk/ColorTest/releases/download/release_v2.7.2/ColorTest_release_2.7.2.apk"
	}],
	"tarball_url": "https://api.github.com/repos/sunzhk/ColorTest/tarball/release_v2.7.2",
	"zipball_url": "https://api.github.com/repos/sunzhk/ColorTest/zipball/release_v2.7.2",
	"body": "1. 新增找相同颜色模式\r\n2. 找不同颜色模式难度优化\r\n3. 新增色彩排序模式\r\n4. 新增结算动画，优化文案"
}
 */