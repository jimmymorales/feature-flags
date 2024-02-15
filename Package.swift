// swift-tools-version:5.3
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://s01.oss.sonatype.org/content/repositories/staging/dev/jimmymorales/feature-flags-core-kmmbridge/0.2.1/feature-flags-core-kmmbridge-0.2.1.zip"
let remoteKotlinChecksum = "627c4f8a3cf6a412f2c3edbdd6f37e402597685b34e124485fee7ee535de666f"
let packageName = "FeatureFlagsCore"
// END KMMBRIDGE BLOCK

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