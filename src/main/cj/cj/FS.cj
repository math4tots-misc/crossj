package cj


# Basic File System API
native class FS {
    def read(path: String): Try[String]
    def readBuffer(path: String): Try[Buffer]
    def write(path: String, data: String)
    def writeBuffer(path: String, data: Buffer)
}
