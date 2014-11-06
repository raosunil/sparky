//
//  ViewController2.h
//  Mayo-ipad
//
//  Created by Nagesh Kumar Uba on 08/10/14.
//  Copyright (c) 2014 Nagesh Kumar Uba. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AssetsLibrary/AssetsLibrary.h>
#import "ViewController.h"


@interface LandingViewController : UIViewController {
    
    IBOutlet UILabel *userlabel;
    IBOutlet UILabel *idlabel;
}
@property (nonatomic, strong) NSArray *Banner;
@property (nonatomic, strong) NSArray *Title;
@property (nonatomic, strong) NSArray *Description;
- (IBAction)LogoutButton:(id)sender;
@end
