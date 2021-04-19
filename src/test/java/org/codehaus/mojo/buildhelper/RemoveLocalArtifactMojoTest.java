package org.codehaus.mojo.buildhelper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.factory.DefaultArtifactFactory;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.rules.TemporaryFolder;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RemoveLocalArtifactMojoTest {

    private static final String metadataContents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" 
            + "<metadata>\n"
            + "<groupId>groupId</groupId>\n" 
            + "<artifactId>artifactId</artifactId>\n" 
            + "<versioning>\n"
            + "<release>3.0.1</release>\n" 
            + "<versions>\n" 
            + "<version>3.0.0</version>\n"
            + "<version>3.0.1</version>\n" 
            + "</versions>\n" 
            + "<lastUpdated>20180709210726</lastUpdated>\n"
            + "</versioning>\n" 
            + "</metadata>";

    @Test
    public void testRemove300() throws MojoFailureException, IOException {

        Path metadataFilePath = Files.createTempFile( "metadata", ".xml" );
        File metadataFile = metadataFilePath.toFile();
        try (PrintWriter out = new PrintWriter( metadataFile.getAbsolutePath() )) {
            out.println( metadataContents );
        }
        MavenProject project = new MavenProject();
        org.apache.maven.artifact.Artifact artifact = new DefaultArtifact( "groupId", "artifactId", "3.0.0", "compile",
                "jar", "default", null );
        project.setArtifact( artifact );

        RemoveLocalArtifactMojo.modifyMetadataFile( project, metadataFile );
        String newMetadata = readFile( metadataFile.toPath() );
        Assert.assertEquals( artifact.getVersion(), "3.0.0" );
        Assert.assertFalse( newMetadata.contains( "<version>3.0.0</version>" ) );
        Assert.assertTrue( newMetadata.contains( "<version>3.0.1</version>" ) );
    }

    @Test
    public void testRemove301() throws MojoFailureException, IOException {

        Path metadataFilePath = Files.createTempFile( "metadata", ".xml" );
        File metadataFile = metadataFilePath.toFile();
        try (PrintWriter out = new PrintWriter( metadataFile.getAbsolutePath() )) {
            out.println( metadataContents );
        }
        MavenProject project = new MavenProject();
        org.apache.maven.artifact.Artifact artifact = new DefaultArtifact( "groupId", "artifactId", "3.0.1", "compile",
                "jar", "default", null );
        project.setArtifact( artifact );

        RemoveLocalArtifactMojo.modifyMetadataFile( project, metadataFile );
        String newMetadata = readFile( metadataFile.toPath() );
        Assert.assertEquals( artifact.getVersion(), "3.0.1" );
        Assert.assertTrue( newMetadata.contains( "<version>3.0.0</version>" ) );
        Assert.assertFalse( newMetadata.contains( "<version>3.0.1</version>" ) );
    }

    private static String readFile(Path path) throws IOException {
        byte[] encoded = Files.readAllBytes( path );
        return new String( encoded, StandardCharsets.UTF_8 );
    }
}
