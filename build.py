#! /usr/bin/python3
import sys
sys.path.append('/Users/admin/Documents/WorkSpace/KF5SDK')
import helper
import getopt

source = '/Users/admin/Documents/WorkSpace/KF5SDK'
sdkPath = 'react-native-kf5sdk/ios/RCTKF5SDK/KF5SDK'
sdkBundle = 'react-native-kf5sdk/ios/RCTKF5SDK/Bundle'
coreSdkPath =  '%s/KF5SDKCore' % sdkPath


def main(argv):
	try:
		#1.打包KF5SDKCore.a
		helper.executiveCommand('pod cache clean KF5SDKCore --all')
		helper.executiveCommand('pod package %s/KF5SDKCore.podspec --library --force' % source, True)

		#2.打包KF5SDK.a
		helper.executiveCommand('pod cache clean KF5SDK --all')
		helper.executiveCommand('pod package %s/KF5SDK.podspec --library --force' % source, True)

		#3.清空文件夹
		helper.clearDirs(sdkPath)
		helper.clearDirs(sdkBundle)

		#4.复制KF5SDKCore.a
		files = helper.findfiles('.','KF5SDKCore-*')
		if len(files) != 1:
			print("没有找到或存在多个KF5SDKCore-*文件夹")
			exit()
		path = files[0]
		helper.copyFile('%s/ios/libKF5SDKCore.a' % path, sdkPath)
		helper.recurseDelDirs(path)

		#5.复制KF5SDK.a
		files = helper.findfiles('.','KF5SDK-*')
		if len(files) != 1:
			print("没有找到或存在多个KF5SDK-*文件夹")
			exit()
		path = files[0]
		helper.copyFile('%s/ios/libKF5SDK.a' % path, sdkPath)
		helper.recurseDelDirs(path)

		#6.复制文件
		helper.copyFiles(getCopyFiles(), sdkPath)

		# #7.复制bundle
		helper.copytree("%s/KF5SDK/UI/Vendors/MJRefresh/MJRefresh.bundle" % source,'%s/MJRefresh.bundle' % sdkBundle)
		helper.copytree("%s/KF5SDK/UI/Vendors/TZImagePickerController/TZImagePickerController.bundle" % source,'%s/TZImagePickerController.bundle' % sdkBundle)
		helper.copytree("%s/KF5SDK/UI/KF5SDK.bundle" % source,'%s/KF5SDK.bundle' % sdkBundle)

		print('😁😁😁😁😁😁😁😁完成任务😁😁😁😁😁😁😁😁')

		#####   npm publish   发布更新
		#####   npm unpublish 包@版本号    例如:npm unpublish xxxx@1.0.0
			
	except Exception as e:
		print(e)
		exit()

def getCopyFiles():
	return [
		"%s/KF5SDK/KFSDK/KF5SDKCore.h" % source,
		"%s/KF5SDK/KFSDK/KFAgent.h" % source,
		"%s/KF5SDK/KFSDK/KFChatManager.h" % source,
		"%s/KF5SDK/KFSDK/KFConfig.h" % source,
		"%s/KF5SDK/KFSDK/KFDispatcher.h" % source,
		"%s/KF5SDK/KFSDK/KFHttpTool.h" % source,
		"%s/KF5SDK/KFSDK/KFLogger.h" % source,
		"%s/KF5SDK/KFSDK/KFMessage.h" % source,

		"%s/KF5SDK/UI/Base/KF5SDK.h" % source,
		"%s/KF5SDK/UI/Base/KFBaseViewController.h" % source,
		"%s/KF5SDK/UI/Base/KFHelper.h" % source,
		"%s/KF5SDK/UI/Base/KFUser.h" % source,
		"%s/KF5SDK/UI/Base/KFUserManager.h" % source,

		"%s/KF5SDK/UI/Doc/KF5SDKDoc.h" % source,
		"%s/KF5SDK/UI/Doc/Controller/KFCategorieListViewController.h" % source,
		"%s/KF5SDK/UI/Doc/Controller/KFDocBaseViewController.h" % source,
		"%s/KF5SDK/UI/Doc/Controller/KFDocumentViewController.h" % source,
		"%s/KF5SDK/UI/Doc/Controller/KFForumListViewController.h" % source,
		"%s/KF5SDK/UI/Doc/Controller/KFPostListViewController.h" % source,
		"%s/KF5SDK/UI/Doc/Models/KFDocItem.h" % source,
		"%s/KF5SDK/UI/Doc/Models/KFDocument.h" % source,

		"%s/KF5SDK/UI/Ticket/KF5SDKTicket.h" % source,
		"%s/KF5SDK/UI/Ticket/TicketList/KFTicket.h" % source,
		"%s/KF5SDK/UI/Ticket/TicketList/KFTicketListViewController.h" % source,
		"%s/KF5SDK/UI/Ticket/TicketList/KFTicketManager.h" % source,
		"%s/KF5SDK/UI/Ticket/CreateTicket/KFCreateTicketViewController.h" % source,
		"%s/KF5SDK/UI/Ticket/TicketView/Model/KFAttachment.h" % source,
		"%s/KF5SDK/UI/Ticket/TicketView/Model/KFComment.h" % source,
		"%s/KF5SDK/UI/Ticket/TicketView/Model/KFRatingModel.h" % source,
		"%s/KF5SDK/UI/Ticket/TicketView/KFDetailMessageViewController.h" % source,
		"%s/KF5SDK/UI/Ticket/TicketView/KFRatingViewController.h" % source,
		"%s/KF5SDK/UI/Ticket/TicketView/KFTicketViewController.h" % source,

		"%s/KF5SDK/UI/Chat/Controller/KFChatViewController.h" % source,
		"%s/KF5SDK/UI/Chat/KF5SDKChat.h" % source,
		"%s/KF5SDK/UI/Chat/viewModel/KFChatViewModel.h" % source,
		"%s/KF5SDK/UI/Chat/viewModel/KFChatVoiceManager.h" % source,
		"%s/KF5SDK/UI/Chat/Model/KFMessageModel.h" % source,
		

		"%s/KF5SDK/UI/Vendors/MLRecorder/libopencore-amrnb.a" % source,
	]

if __name__=="__main__":
    main(sys.argv[1:])
