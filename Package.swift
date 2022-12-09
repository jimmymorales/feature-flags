// swift-tools-version:5.3
import PackageDescription

let remoteKotlinUrl = "https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/devjimmymorales-1009//dev/jimmymorales/core-kmmbridge/0.2.0/core-kmmbridge-0.2.0.zip"
let remoteKotlinChecksum = "7020c9684f5a463a9b0771f864a75aae65f3110f99bcde735f55ffe987314e19"
let packageName = "FeatureFlagsCore"

let package = Package(
    name: packageName,
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: packageName,
            targets: [packageName]
        ),
    ],
    targets: [
        .binaryTarget(
            name: packageName,
            url: remoteKotlinUrl,
            checksum: remoteKotlinChecksum
        )
        ,
    ]
)