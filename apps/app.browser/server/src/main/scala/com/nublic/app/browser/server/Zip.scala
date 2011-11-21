package com.nublic.app.browser.server

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.commons.io.IOUtils
import java.io.FileInputStream

object Zip {
  
  def zip(filename: String): ByteArrayOutputStream = {
    val file = new File(filename)
    if (file.isDirectory()) {
      zipFolder(file)
    } else {
      zipFile(file)
    }
  }
  
  def zipFile(file_to_add: File): ByteArrayOutputStream = {
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
  
  def zipFolder(folder_to_add: File): ByteArrayOutputStream = {
    val bytes = new ByteArrayOutputStream()
    val zip_stream = new ZipOutputStream(bytes)
    add_folder_contents(folder_to_add, folder_to_add.getAbsolutePath(), zip_stream)
    zip_stream.close()
    bytes
  }
  
  def zipFileSeq(files_to_add: Seq[File], base_path: String): ByteArrayOutputStream = {
    val bytes = new ByteArrayOutputStream()
    val zip_stream = new ZipOutputStream(bytes)
    add_files(files_to_add, base_path, zip_stream)
    zip_stream.close()
    bytes
  }
  
  private def add_folder_contents(folder_to_add: File, base_path: String, zip_stream: ZipOutputStream): Unit =
    add_files(folder_to_add.listFiles(), base_path, zip_stream)
  
  private def add_files(files_to_add: Seq[File], base_path: String, zip_stream: ZipOutputStream): Unit = {
    for(file <- files_to_add) {
      if(!is_hidden(file.getName())) {
        if(file.isDirectory()) {
          add_folder_contents(file, base_path, zip_stream)
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