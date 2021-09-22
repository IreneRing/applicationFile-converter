package com.plugins.appFileConverter.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.plugins.appFileConverter.consts.Constant;
import com.plugins.appFileConverter.consts.MsgConsts;

/**
 * @author xqchen
 */
public class CommonUtils {
    /**
     * get selected file
     *
     * @param event
     * @param showNotifications
     * @return PsiFile
     */
    public static PsiFile getSelectedFile(AnActionEvent event, boolean showNotifications) {
        //获取当前打开的文件
        PsiFile selectedFile = event.getData(LangDataKeys.PSI_FILE);
        if (selectedFile == null) {
            if (showNotifications) {
                Notifications.Bus.notify(new Notification(Constant.GROUP_DISPLAY_ID, MsgConsts.NO_FILE_SELECTED,
                        MsgConsts.SELECT_FILE_FIRST, NotificationType.ERROR));
            }
            return null;
        }

        return selectedFile;
    }

    /**
     * get file type
     *
     * @param event
     * @param showNotifications
     */
    public static String getFileType(AnActionEvent event, boolean showNotifications) {
        //获取打开的文件
        PsiFile selectedFile = getSelectedFile(event, showNotifications);
        if (null == selectedFile) {
            return null;
        } else {
            return selectedFile.getFileType().getName();
        }

    }

    /**
     * contain file type
     *
     * @param event
     * @param showNotifications
     */
    public static boolean containFileType(AnActionEvent event, String fileType) {
        //获取打开的文件
        PsiFile selectedFile = getSelectedFile(event, false);
        if (null == selectedFile) {
            return false;
        } else {
            return selectedFile.getFileType().getName().equals(fileType);
        }

    }

    /**
     * has the same file name
     *
     * @param event
     */
    public static boolean isSameFileName(AnActionEvent event) {
        //获取打开的文件
        PsiFile selectedFile = getSelectedFile(event, false);
        if (null == selectedFile) {
            return false;
        } else {
            PsiDirectory parent = selectedFile.getParent();
            String fileType = selectedFile.getFileType().getName();
            String sufFile = "";
            if (Constant.YAML.equals(fileType)) {
                sufFile = Constant.PROPERTIES_SUFFIX;
            }else if (Constant.PROPERTIES.equals(fileType)) {
                sufFile = Constant.YAML_SUFFIX;
            }

            assert parent != null;
            return null != parent.findFile(selectedFile.getName().split("\\.")[0] + sufFile);

        }

    }

    /**
     * create a VirtualFile
     *
     * @param file
     * @param prefix
     * @param suffix
     */
    public static VirtualFile createVirtualFile(PsiFile file, String prefix, String suffix) {
        PsiDirectory psiDirectory = file.getParent();
        String fileName = prefix + suffix;
        if (null != psiDirectory.findFile(fileName)){
            fileName = prefix + "_new" + suffix;
        }
        PsiFile newFile = psiDirectory.createFile(fileName);
        return newFile.getVirtualFile();
    }

    /**
     * create a VirtualFile
     *
     * @param event
     * @param prefix
     * @param suffix
     */
    public static VirtualFile createVirtualFile(AnActionEvent event, String prefix, String suffix) {
        PsiFile selectedFile = getSelectedFile(event, false);
        if (null == selectedFile) {
            return null;
        }else {
            return createVirtualFile(selectedFile,prefix,suffix);
        }
    }

    /**
     * get a VirtualFile
     *
     * @param event
     */
    public static VirtualFile getVirtualFile(AnActionEvent event) {
        // get file type
        final String fileType = CommonUtils.getFileType(event, true);
        final PsiFile selectedFile = CommonUtils.getSelectedFile(event, true);
        if (StringUtil.isEmpty(fileType) || null == selectedFile) {
            return null;
        }
        return selectedFile.getVirtualFile();
    }
}
