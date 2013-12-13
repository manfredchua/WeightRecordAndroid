package com.squad22.fit.utils;

import java.io.File;
import android.content.Context;

public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        for(File f:files)
            f.delete();
    }
    
    public static void CreateText(){ 

        File file = new File(Constants.DOWNLOAD_PHOTO_PATH); 

        if (!file.exists()) { 

            try { 

                //按照指定的路径创建文件夹 

                file.mkdirs(); 

            } catch (Exception e) { 

                // TODO: handle exception 

            } 

        } 
//
//        File dir = new File(path); 
//
//        if (!dir.exists()) { 
//
//              try { 
//
//                  //在指定的文件夹中创建文件 
//
//                  dir.createNewFile(); 
//
//            } catch (Exception e) { 
//
//            } 
//
//        } 

 

    }


}