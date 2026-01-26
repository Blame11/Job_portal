const express = require("express");
const ApplicationRouter = express.Router();

const {
    authenticateUser,
} = require("./../Middleware/UserAuthenticationMiddleware");

// Controllers
const ApplicationController = require("../Controller/ApplicationController");

// Middlewares
const { checkInput } = require("../Validation/ApplicationDataRules");
const {
    inputValidationMiddleware,
} = require("../Validation/ValidationMiddleware");
const {
    userAuthorizationHandler,
} = require("./../Middleware/UserAuthorizationMiddleware");

// Authentication routes

ApplicationRouter.get(
    "/applicant-jobs",
    userAuthorizationHandler("user"),
    ApplicationController.getCandidateAppliedJobs
);

ApplicationRouter.post(
    "/apply",
    (req, res, next) => {
        // Use multer from app context, with single file (optional)
        req.app.upload.single("resume")(req, res, (err) => {
            if (err && err.message !== "Only PDF and DOC files are allowed") {
                // Only file type errors should be handled, missing file is OK
                if (req.file) {
                    return next(err);
                }
            } else if (err) {
                return next(err);
            }
            next();
        });
    },
    checkInput,
    inputValidationMiddleware,
    userAuthorizationHandler("user"),
    ApplicationController.applyInJob
);

ApplicationRouter.get(
    "/recruiter-jobs",
    userAuthorizationHandler("recruiter"),
    ApplicationController.getRecruiterPostJobs
);

ApplicationRouter.patch(
    "/:id",
    userAuthorizationHandler("recruiter"),
    ApplicationController.updateJobStatus
);

ApplicationRouter.get(
    "/:applicationId/resume",
    userAuthorizationHandler("user", "recruiter"),
    ApplicationController.getResumeFile
);

module.exports = ApplicationRouter;
