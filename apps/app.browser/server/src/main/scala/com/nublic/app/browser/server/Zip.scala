package com.nublic.app.browser.server

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.commons.io.IOUtils
import java.io.FileInputStream
import com.nublic.filesAndUsers.java.User

object Zip {
  
  def zip(filename: String, user: User): ByteArrayOutputStream = zip(new File(filename), user)
  
  def zip(file: File, user: User): ByteArrayOutputStream = {
    if (file.isDirectory()) {
      zipFolder(file, user)
    } else {
      zipFile(file, user)
    }
  }
  
  def zipFile(file_to_add: File, user: User): ByteArrayOutputStream = {
    if (!user.canRead(file_to_add)) {
      throw new IllegalArgumentException()
    }
    val bytes = new ByteArrayOutputStream()
    val zip_stream = new ZipOutputStream(bytes)
    val zip_entry = new ZipEntry(file_to_add.getName())
    zip_stream.putNextEntry(zip_entry)
    val file_stream = new FileInputStream(file_to_add)
    IOUtils.copy(file_stream, zip_stream)
    file_stream.close()
    zip_stream.closeEntry()
    zip_stream.close()
    bytes
  }
  
  def zipFolder(folder_to_add: File, user: User): ByteArrayOutputStream = {
    if (!user.canRead(folder_to_add)) {
      throw new IllegalArgumentException()
    }
    val bytes = new ByteArrayOutputStream()
    val zip_stream = new ZipOutputStream(bytes)
    add_folder_contents(folder_to_add, folder_to_add.getAbsolutePath() + "/", zip_stream, user)
    zip_stream.close()
    bytes
  }
  
  def zipFileSeq(files_to_add: Seq[File], base_path: String, user: User): ByteArrayOutputStream = {
    val bytes = new ByteArrayOutputStream()
    val zip_stream = new ZipOutputStream(bytes)
    add_files(files_to_add, base_path, zip_stream, user)
    zip_stream.close()
    bytes
  }
  
  private def add_folder_contents(folder_to_add: File, base_path: String, zip_stream: ZipOutputStream, user: User): Unit =
    add_files(folder_to_add.listFiles(), base_path, zip_stream, user)
  
  private def add_files(files_to_add: Seq[File], base_path: String, zip_stream: ZipOutputStream, user: User): Unit = {
    for(file <- files_to_add) {
      if(!is_hidden(file.getName()) && user.canRead(file)) {
        if(file.isDirectory()) {
          add_folder_contents(file, base_path, zip_stream, user)
        } else {
          val zip_entry = new ZipEntry(file.getAbsolutePath().substring(base_path.length()))
          zip_stream.putNextEntry(zip_entry)
          val file_stream = new FileInputStream(file)
          IOUtils.copy(file_stream, zip_stream)
          file_stream.close()
          zip_stream.closeEntry()
        }
      }
    }
  }
  
  private def is_hidden(filename: String) = filename.startsWith(".") || filename.endsWith("~")
}