# RN封装图片预览组件使用指南
## 函数简述

---
* 此模块名为：`PicturePreview`
* 包含RN组件名为：`PicturePreview`

### RN Module使用函数说明
#### 点击使用图片预览组件
函数名 |传入参数| 描述
:---------: | :---------: | :-------------
openPreview | imageData, index| 传入图片数组与起始预览值

### iOS 安装和修改MWPhotoBrowser说明

1. pod "MWPhotoBrowser"

2. RCTImagePreview.xcodeproj -> Build Settings -> Framework Search Paths添加`"$(SRCROOT)/../../../ios/Pods/MWPhotoBrowser"`; Header Search Paths添加`"$(SRCROOT)/../../../ios/Pods/MWPhotoBrowser"`, `non-recursive`改成`recursive`

3. Pods -> Pods -> MWPhotoBrowser文件夹里修改`MWPhotoBrowserPrivate.h`文件第37行
```
UIBarButtonItem *_previousButton, *_nextButton, *_actionButton, *_doneButton, *_saveButton;
```

4. Pods -> Pods -> MWPhotoBrowser文件夹里`MWPhotoBrowser.m`文件查找`if ([self.navigationController.viewControllers objectAtIndex:0] == self)`将if判断里面的代码替换为下面的
```
// We're first on stack so show done button
_doneButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"取消", nil) style:UIBarButtonItemStylePlain target:self action:@selector(doneButtonPressed:)];
// Set appearance
[_doneButton setBackgroundImage:nil forState:UIControlStateNormal barMetrics:UIBarMetricsDefault];
[_doneButton setBackgroundImage:nil forState:UIControlStateNormal barMetrics:UIBarMetricsLandscapePhone];
[_doneButton setBackgroundImage:nil forState:UIControlStateHighlighted barMetrics:UIBarMetricsDefault];
[_doneButton setBackgroundImage:nil forState:UIControlStateHighlighted barMetrics:UIBarMetricsLandscapePhone];
[_doneButton setTitleTextAttributes:[NSDictionary dictionary] forState:UIControlStateNormal];
[_doneButton setTitleTextAttributes:[NSDictionary dictionary] forState:UIControlStateHighlighted];
self.navigationItem.leftBarButtonItem = _doneButton;

_saveButton = [[UIBarButtonItem alloc]initWithTitle:NSLocalizedString(@"保存", nil) style:UIBarButtonItemStylePlain target:self action:@selector(saveButtonPressed:)];
[_saveButton setBackgroundImage:nil forState:UIControlStateNormal barMetrics:UIBarMetricsDefault];
[_saveButton setBackgroundImage:nil forState:UIControlStateNormal barMetrics:UIBarMetricsLandscapePhone];
[_saveButton setBackgroundImage:nil forState:UIControlStateHighlighted barMetrics:UIBarMetricsDefault];
[_saveButton setBackgroundImage:nil forState:UIControlStateHighlighted barMetrics:UIBarMetricsLandscapePhone];
[_saveButton setTitleTextAttributes:[NSDictionary dictionary] forState:UIControlStateNormal];
[_saveButton setTitleTextAttributes:[NSDictionary dictionary] forState:UIControlStateHighlighted];
self.navigationItem.rightBarButtonItem = _saveButton;
```
5. 在`MWPhotoBrowser.m`文件191行后换行添加
```
UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(doTapChange)];
tapGesture.numberOfTapsRequired = 1;
[self.view addGestureRecognizer:tapGesture];
```
6. 在`MWPhotoBrowser.m`文件```#pragma mark - Misc```后换行添加
```
-(void)doTapChange{
[self dismissViewControllerAnimated:YES completion:nil];
}

- (void)saveButtonPressed:(id)sender {

NSArray *data = [[NSUserDefaults standardUserDefaults] objectForKey:@"imageData"];

UIImageWriteToSavedPhotosAlbum([UIImage imageWithData:[NSData dataWithContentsOfURL:[NSURL URLWithString:data[_currentPageIndex]]]], self, @selector(image:didFinishSavingWithError:contextInfo:), (__bridge void *)self);
}

- (void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo
{
if (error == NULL) {
UILabel * tipLabel = [[UILabel alloc] initWithFrame:CGRectMake(([[UIScreen mainScreen] bounds].size.width - 80) / 2 , ([[UIScreen mainScreen] bounds].size.height - 30) / 2, 80, 30)];
[tipLabel setText:@"保存成功"];
tipLabel.backgroundColor = [UIColor blackColor];
tipLabel.layer.cornerRadius = 5;
tipLabel.layer.masksToBounds = YES;
tipLabel.textAlignment = NSTextAlignmentCenter;
tipLabel.textColor = [UIColor whiteColor];
[self.view addSubview:tipLabel];
[UIView animateWithDuration:2.0 animations:^{
tipLabel.alpha = 0.0;
} completion:^(BOOL finished) {
[tipLabel removeFromSuperview];
}];
}
}
```

7. 在`MWPhotoBrowser.m`文件查找```[self.navigationController.navigationBar setAlpha:alpha];```后注释

8. 在`MWPhotoBrowser.m`文件查找```[[UIApplication sharedApplication] setStatusBarHidden:hidden withAnimation:animated ? UIStatusBarAnimationSlide : UIStatusBarAnimationNone];```后注释
