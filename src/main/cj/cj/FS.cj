package cj


# Basic File System API
native class FS {
    def read(path: String): Try[String]
    def readBuffer(path: String): Try[Buffer]
    def write(path: String, data: String)
    def writeBuffer(path: String, data: Buffer)

    def sep(): String
    def join(parts: List[String]): String
    def dirname(path: String): String
    def basename(path: String): String

    def cwd(): String
    def exists(path: String): Bool
    def isfile(path: String): Bool
    def isdir(path: String): Bool
    def list(path: String): Try[List[String]]
}
