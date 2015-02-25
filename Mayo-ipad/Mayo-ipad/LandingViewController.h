//
//  ViewController2.h
//  Mayo-ipad
//
//  Created by Sunil Rao, Nagesh Kumar Uba et al on 08/10/14.
//  Copyright (c) 2014 Nagesh Kumar Uba. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AssetsLibrary/AssetsLibrary.h>
#import "ViewController.h"


@interface LandingViewController : UIViewController {
    __weak IBOutlet UIButton *libraryaccess;
    __weak IBOutlet UIButton *nexttoupload;
    
}
- (IBAction)libraryaccess:(id)sender;

@end
