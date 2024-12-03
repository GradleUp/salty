package salty.core

import gratatouille.GInputFiles
import gratatouille.GOutputFile
import gratatouille.GTaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@GTaskAction(
  description = "Scans files for calls to forbiddenMethods"
)
internal fun scanClassPath(files: GInputFiles, forbiddenMethods: List<String>, output: GOutputFile) {
  val context = Context(forbiddenMethods)
  files.forEach {
    when (it.file.extension) {
      "aar", "jar" -> scanJar(it.file, context)
      "class" -> scanFile(it.file, context)
      else -> {
        println("Ignoring file: ${it.file.name}")
      }
    }
  }

  if (context.issues.isNotEmpty()) {
    context.issues.forEach {
      println("e: $it")
    }
    error("Salty found forbidden methods")
  }
  output.writeText("processed ${files.count()} files, no forbidden method found")
}

private fun scanJar(jar: File, context: Context) {
  ZipInputStream(jar.inputStream()).use { zipInputStream ->
    var entry: ZipEntry? = zipInputStream.nextEntry
    while (entry != null) {
      if (!entry.isDirectory) {
        if (entry.name.endsWith(".class")) {
          processBytes(zipInputStream.readAllBytes(), context, "${jar.absolutePath}:${entry.name}")
        } else {
          println("Cannot process ${entry.name}")
        }
      }
      entry = zipInputStream.nextEntry
    }
  }
}

fun processBytes(bytes: ByteArray, context: Context, location: String) {
  println("processing $location")
  ClassReader(bytes).accept(object : ClassVisitor(Opcodes.ASM9) {
    override fun visitMethod(
      access: Int,
      name: String?,
      descriptor: String?,
      signature: String?,
      exceptions: Array<out String?>?
    ): MethodVisitor? {
      return MyMethodVisitor(context, location)
    }
  }, 0)
}

private class MyMethodVisitor(private val context: Context, private val location: String) : MethodVisitor(Opcodes.ASM9) {
  override fun visitMethodInsn(opcode: Int, owner: String?, name: String?, descriptor: String?, isInterface: Boolean) {
    val method = owner?.replace('/', '.')?.plus('.')?.plus(name)
    if (context.forbiddenMethods.contains("$method")) {
      context.issues.add("$location uses $method")
    }
  }
}

class Context(
  val forbiddenMethods: List<String>
) {
  val issues = mutableListOf<String>()
}

private fun scanFile(file: File, context: Context) {
  processBytes(file.readBytes(), context, "${file.absolutePath}")
}
