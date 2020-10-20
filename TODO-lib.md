Whereas TODO.md is about language/core features, TODO-lib is just a list of libraries
I'm thinking of adding to `support/shared` (heh, you know, the fun stuff)



* CRC32 function. Maybe class name `crossj.hacks.CRC32`?
    * I need this to implement `Bitmap.toPNGBytes` (each 'chunk' requires a crc32 checksum)
    * considerations:
        * so if I have to return `int`, the return value must be signed CRC32.
            Maybe I want to allow alternatively return a `double` so that I
            can return an unsigned version?
            (or maybe even just support `long`).
* `Bitmap.toPNGBytes`. ~~Seems pretty simple, I just need crc32~~ I have crc32 now, but I also
    need DEFLATE. For filtering, I can prepend each scanline with a 0 byte to mostly ignore it for now.

* Little ray tracer by working through the "The Ray Tracer Challenge" book.
    * Maybe under `crossj.hacks.ray` package?
    * I'll need some sort of `Matrix` class. Unclear whether I want to allow
        substituting with host platform's linear algebra library.
        I don't think there's one in vanilla javascript (yet).
